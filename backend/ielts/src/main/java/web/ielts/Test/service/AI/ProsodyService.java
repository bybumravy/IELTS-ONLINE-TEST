    package web.ielts.Test.service.AI;
    import com.fasterxml.jackson.databind.JsonNode;
    import org.springframework.stereotype.Service;

    import java.io.*;
    import java.net.URL;
    import java.nio.file.Files;
    import java.time.Instant;
    import java.util.*;
    import java.util.concurrent.atomic.AtomicInteger;

    @Service
    public class ProsodyService {

            private final String PRAAT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\praat6438_win-intel64\\Praat.exe";
            private final String PRAAT_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script.praat"; // Script Praat
            private final String STRESS_ANALYSIS_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\stressAnalysis.praat";
            private double praatGetAudioDuration(File wavFile) throws IOException {
            // Lấy đường dẫn tuyệt đối cho script Praat
            String scriptPath = new File("D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\getDuration.praat").getAbsolutePath();
            System.out.println("Praat path: " + PRAAT_PATH);
            System.out.println("Praat script: " + scriptPath);
            System.out.println("Audio file: " + wavFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run", scriptPath, wavFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);  // gom stderr về stdout cho dễ debug

            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            String durationLine = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    if (durationLine == null) durationLine = line;
                }
            }

            int exitCode;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                throw new IOException("Process bị gián đoạn", e);
            }

            if (exitCode != 0) {
                System.err.println("Praat output:\n" + output);
                throw new IOException("1.Praat exited with code " + exitCode);
            }

            if (durationLine == null || durationLine.trim().isEmpty()) {
                System.err.println("2.Praat output:\n" + output);
                throw new IOException("Không nhận được duration từ Praat output");
            }

            try {
                String cleanDuration = durationLine.replaceAll("[^0-9.]", "");
                return Double.parseDouble(cleanDuration);
            } catch (NumberFormatException e) {
                System.err.println("3.Praat output:\n" + output);
                throw new IOException("Không parse được duration: " + durationLine, e);
            }

        }
        private File generateTextGridFromJson(JsonNode root, File audioFile) throws IOException {
            File textGridFile = new File(audioFile.getParent(), audioFile.getName().replace(".wav", ".TextGrid"));
            System.out.println("👉 Bắt đầu generate TextGrid file: " + textGridFile.getAbsolutePath());

            try (PrintWriter writer = new PrintWriter(textGridFile)) {
                double audioDuration = praatGetAudioDuration(audioFile);
                System.out.println("✅ Audio duration: " + audioDuration);

                writer.println("File type = \"ooTextFile\"");
                writer.println("Object class = \"TextGrid\"");
                writer.println();
                writer.println("xmin = 0");
                writer.println("xmax = " + audioDuration);
                writer.println("tiers? <exists>");
                writer.println("size = 2"); // Now we have 2 tiers: words and sentences
                writer.println("item []:");

                // Word tier (same as before)
                writer.println("    item [1]:");
                writer.println("        class = \"IntervalTier\"");
                writer.println("        name = \"words\"");
                writer.println("        xmin = 0");
                writer.println("        xmax = " + audioDuration);

                List<JsonNode> wordNodes = new ArrayList<>();
                if (root.has("segments")) {
                    root.get("segments").forEach(segment ->
                            segment.get("words").forEach(wordNodes::add)
                    );
                } else if (root.has("words")) {
                    root.get("words").forEach(wordNodes::add);
                }
                System.out.println("✅ Tổng số từ: " + wordNodes.size());

                writer.println("        intervals: size = " + wordNodes.size());
                for (int i = 0; i < wordNodes.size(); i++) {
                    JsonNode word = wordNodes.get(i);
                    double start = word.get("start").asDouble();
                    double end = word.get("end").asDouble();
                    String wordText = word.get("word").asText();
                    writer.println("        intervals [" + (i + 1) + "]:");
                    writer.println("            xmin = " + start);
                    writer.println("            xmax = " + end);
                    writer.println("            text = \"" + wordText + "\"");
                }

                // New: Sentence tier
                writer.println("    item [2]:");
                writer.println("        class = \"IntervalTier\"");
                writer.println("        name = \"sentences\"");
                writer.println("        xmin = 0");
                writer.println("        xmax = " + audioDuration);

                // Group words into sentences (simple implementation - you may need to adjust)
                List<List<JsonNode>> sentences = groupWordsIntoSentences(wordNodes);
                writer.println("        intervals: size = " + sentences.size());

                for (int i = 0; i < sentences.size(); i++) {
                    List<JsonNode> sentenceWords = sentences.get(i);
                    double sentenceStart = sentenceWords.get(0).get("start").asDouble();
                    double sentenceEnd = sentenceWords.get(sentenceWords.size()-1).get("end").asDouble();

                    writer.println("        intervals [" + (i + 1) + "]:");
                    writer.println("            xmin = " + sentenceStart);
                    writer.println("            xmax = " + sentenceEnd);
                    writer.println("            text = \"Sentence " + (i+1) + "\"");
                }

                System.out.println("✅ Ghi file TextGrid thành công: " + textGridFile.getAbsolutePath());
            }
            return textGridFile;
        }
        private List<List<JsonNode>> groupWordsIntoSentences(List<JsonNode> words) {
            List<List<JsonNode>> sentences = new ArrayList<>();
            List<JsonNode> currentSentence = new ArrayList<>();

            for (JsonNode word : words) {
                currentSentence.add(word);
                String wordText = word.get("word").asText().toLowerCase();
                // Simple heuristic: sentence ends with period, question mark, or exclamation
                if (wordText.matches(".*[.!?]$")) {
                    sentences.add(currentSentence);
                    currentSentence = new ArrayList<>();
                }
            }

            if (!currentSentence.isEmpty()) {
                sentences.add(currentSentence);
            }

            return sentences;
        }



        public Map<String, Object> analyze(String audioUrl, JsonNode root) {
            System.out.println("\n=======================================");
            System.out.println("🚀 STARTING PROSODY ANALYSIS");
            System.out.println("   Audio URL: " + audioUrl);
            System.out.println("   JSON data: " + root.toString());
            System.out.println("=======================================\n");
            Map<String, Object> result = new HashMap<>();
            try {
                // 1. Tải và chuyển đổi file âm thanh
                File mp3File = downloadAudioFile(audioUrl);
                File wavFile = convertMp3ToWav(mp3File);

                // 2. Tạo TextGrid
                File textGridFile = generateTextGridFromJson(root, wavFile);

                // 3. Phân tích prosody cơ bản
                System.out.println("Thong so co ban");
                Map<String, Double> praatResults = runPraatAnalysis(wavFile, textGridFile);

                // 4. Phân tích trọng âm từ (CHI TIẾT VỊ TRÍ)
                System.out.println("Trong am");
                Map<String, Object> stressResults = analyzeWordStress(wavFile, textGridFile, root);

                // 5. Phân tích ngữ điệu câu (MỚI)
                System.out.println("Ngu dieu cau");
                Map<String, Object> intonationResults = analyzeSentenceIntonation(wavFile, textGridFile);

                // 6. Tính điểm tổng hợp
                Map<String, Object> pronunciationScore = evaluatePronunciation(
                        praatResults,
                        stressResults,
                        intonationResults
                );

                // 7. Tổng hợp kết quả
                result.putAll(pronunciationScore);
                result.put("wordStressDetails", stressResults.get("wordStressDetails"));
                result.put("intonation", intonationResults);
                result.put("prosodyFeatures", praatResults);
                System.out.println("\n=======================================");
                System.out.println("🎉 ANALYSIS COMPLETED SUCCESSFULLY");
                System.out.println("   Final result: " + result);
                System.out.println("=======================================");
                return result;
            } catch (Exception e) {
                System.err.println("❌ ANALYSIS ERROR: " + e.getMessage());
                e.printStackTrace();
                return Map.of("error", e.getMessage(), "stackTrace", Arrays.toString(e.getStackTrace()));
            }
        }


        private double calculateStressScore(Map<String, Object> stressResults) {
            List<Map<String, Object>> words = (List<Map<String, Object>>) stressResults.get("wordStressDetails");
            if (words == null) return 0;

            // Đơn giản: tính tỷ lệ từ có intensity > ngưỡng
            long stressedWords = words.stream()
                    .filter(w -> (double)w.get("maxIntensity") > 70)
                    .count();

            return (double)stressedWords / words.size() * 9; // Scale to 0-9
        }

        private double calculateIntonationScore(Map<String, Object> intonationResults) {
            String type = (String) intonationResults.get("intonationType");
            switch (type) {
                case "rising": return 8.0; // Ngữ điệu lên tốt cho câu hỏi
                case "falling": return 9.0; // Ngữ điệu xuống tốt cho câu trần thuật
                default: return 6.0; // Ngữ điệu bằng
            }
        }
        private Map<String, Object> evaluatePronunciation(
                Map<String, Double> praatResults,
                Map<String, Object> stressResults,
                Map<String, Object> intonationResults) {

            Map<String, Object> evaluation = new HashMap<>();

            // 1. Đánh giá trọng âm từ
            System.out.println("TinhtoanDiemTrongAM");
            double stressScore = calculateStressScore(stressResults);
            evaluation.put("stressScore", stressScore);

            // 2. Đánh giá ngữ điệu câu
            System.out.println("TinhtoanDiemNguDieu");
            double intonationScore = calculateIntonationScore(intonationResults);
            evaluation.put("intonationScore", intonationScore);

            // 3. Tính điểm tổng hợp
            double overallScore = (
                    stressScore * 0.4 +
                            intonationScore * 0.4 +
                            praatResults.get("speechRate") * 0.2
            );

            evaluation.put("pronunciationBandScore", Math.round(overallScore * 2) / 2.0);
            return evaluation;
        }



        private Map<String, Object> analyzeWordStress(File wavFile, File textGridFile, JsonNode root)
                throws IOException, InterruptedException {

            // Tạo file output trong thư mục cùng với input files
            File outputFile = new File(textGridFile.getParent(), "stress_output_" + System.currentTimeMillis() + ".txt");
            System.out.println("\n🎯 [STRESS ANALYSIS] Starting word stress analysis...");
            System.out.println("   Output file: " + outputFile.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run",
                    STRESS_ANALYSIS_SCRIPT_PATH,
                    wavFile.getAbsolutePath(),
                    textGridFile.getAbsolutePath(),
                    outputFile.getAbsolutePath()
            );
            System.out.println("⚡ [STRESS] Command: " + String.join(" ", pb.command()));
            Process process = pb.start();

            System.out.println("🔍 [STRESS OUTPUT] Real-time output:");
            // Đọc output để debug
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PRAAT OUTPUT] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("✅ [STRESS] Process exited with code: " + exitCode);

            if (exitCode != 0) {
                System.err.println("❌ [ERROR] Stress analysis failed!");
                // Đọc nội dung file output nếu có để debug
                if (outputFile.exists()) {
                    System.err.println("📜 [ERROR DETAILS] Output file content:");
                    Files.lines(outputFile.toPath()).forEach(line -> System.err.println("   " + line));
                }
                throw new RuntimeException("Stress analysis failed with exit code: " + exitCode);
            }
            System.out.println("📊 [STRESS] Parsing results...");
            return parseStressOutput(outputFile, root);
        }

        private Map<String, Object> analyzeSentenceIntonation(File wavFile, File textGridFile) {
            Map<String, Object> results = new HashMap<>();
            List<Map<String, Object>> sentenceIntonations = new ArrayList<>();

            try {
                File outputFile = new File(textGridFile.getParent(),
                        "intonation_output_" + System.currentTimeMillis() + ".txt");

                ProcessBuilder pb = new ProcessBuilder(
                        PRAAT_PATH, "--run",
                        PRAAT_SCRIPT_PATH.replace(".praat", "_intonation.praat"),
                        wavFile.getAbsolutePath(),
                        textGridFile.getAbsolutePath(),
                        outputFile.getAbsolutePath()
                );
                pb.redirectErrorStream(true);

                Process process = pb.start();

                // Read output for debugging
                StringBuilder praatOutput = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        praatOutput.append(line).append("\n");
                        System.out.println("   [PRAAT] " + line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0 || !outputFile.exists()) {
                    System.err.println("❌ Intonation analysis failed");
                    System.err.println("   Exit code: " + exitCode);
                    System.err.println("   Output:\n" + praatOutput);
                    return Map.of("error", "Intonation analysis failed");
                }

                // Parse the output file with sentence-level results
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("SENTENCE")) {
                            Map<String, Object> sentenceData = new HashMap<>();
                            String[] parts = line.split("\\|");

                            // Example line format:
                            // SENTENCE 1|start=1.23|end=3.45|pitchStart=120|pitchEnd=110|type=falling
                            sentenceData.put("sentenceNumber", Integer.parseInt(parts[0].split(" ")[1]));
                            sentenceData.put("start", Double.parseDouble(parts[1].split("=")[1]));
                            sentenceData.put("end", Double.parseDouble(parts[2].split("=")[1]));
                            sentenceData.put("pitchStart", Double.parseDouble(parts[3].split("=")[1]));
                            sentenceData.put("pitchEnd", Double.parseDouble(parts[4].split("=")[1]));
                            sentenceData.put("intonationType", parts[5].split("=")[1]);

                            sentenceIntonations.add(sentenceData);
                        }
                    }
                }

                results.put("sentences", sentenceIntonations);
                results.put("analysisSuccess", true);

            } catch (Exception e) {
                System.err.println("💥 Sentence intonation analysis error: " + e.getMessage());
                results.put("error", e.getMessage());
                results.put("analysisSuccess", false);
            }

            return results;
        }


        private Map<String, Object> parseIntonationOutput(File outputFile) throws IOException {
            Map<String, Object> results = new HashMap<>();
            System.out.println("🔎 [INTONATION] Reading output file: " + outputFile.getAbsolutePath());

            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("   Processing line: " + line); // Log từng dòng

                    if (line.startsWith("PITCH_START=")) {
                        double pitchStart = Double.parseDouble(line.split("=")[1]);
                        results.put("pitchStart", pitchStart);
                        System.out.println("   Detected pitch start: " + pitchStart);

                    } else if (line.startsWith("PITCH_END=")) {
                        double pitchEnd = Double.parseDouble(line.split("=")[1]);
                        results.put("pitchEnd", pitchEnd);
                        System.out.println("   Detected pitch end: " + pitchEnd);

                    } else if (line.startsWith("PITCH_DIFF=")) {
                        double pitchDiff = Double.parseDouble(line.split("=")[1]);
                        results.put("pitchDifference", pitchDiff);
                        System.out.println("   Pitch difference: " + pitchDiff);
                    }
                }
            }

            // Xác định loại ngữ điệu
            double pitchDiff = (double)results.getOrDefault("pitchDifference",
                    (double)results.get("pitchEnd") - (double)results.get("pitchStart"));

            String intonationType;
            if (pitchDiff > 20) {
                intonationType = "rising";
            } else if (pitchDiff < -20) {
                intonationType = "falling";
            } else {
                intonationType = "flat";
            }

            results.put("intonationType", intonationType);
            System.out.println("🎙️ Detected intonation type: " + intonationType);

            // Thêm thông tin metadata
            results.put("analysisTime", Instant.now().toString());
            results.put("outputFile", outputFile.getAbsolutePath());

            return results;
        }

        private Map<String, Object> parseStressOutput(File outputFile, JsonNode root) throws IOException {
            Map<String, Object> results = new HashMap<>();
            List<Map<String, Object>> wordStressList = new ArrayList<>();

            System.out.println("\n📊 [STRESS] Bắt đầu đọc file kết quả: " + outputFile.getAbsolutePath());

            if (!outputFile.exists()) {
                System.err.println("❌ ERROR: File kết quả không tồn tại!");
                return Map.of("error", "Stress output file not found");
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                int lineCount = 0;

                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    System.out.println("   [Dòng " + lineCount + "] " + line);

                    if (line.startsWith("WORD_STRESS:")) {
                        String[] parts = line.split(":");
                        if (parts.length >= 6) {
                            try {
                                Map<String, Object> wordStress = new HashMap<>();
                                String word = parts[1].trim();
                                double maxIntensity = Double.parseDouble(parts[2].trim());
                                double stressPosition = Double.parseDouble(parts[3].trim());
                                double start = Double.parseDouble(parts[4].trim());
                                double end = Double.parseDouble(parts[5].trim());

                                wordStress.put("word", word);
                                wordStress.put("maxIntensity", maxIntensity);
                                wordStress.put("stressPosition", stressPosition);
                                wordStress.put("start", start);
                                wordStress.put("end", end);

                                wordStressList.add(wordStress);

                                System.out.println("   ✅ Đã xử lý từ: " + word);
                                System.out.println("      - Cường độ: " + maxIntensity);
                                System.out.println("      - Vị trí trọng âm: " + stressPosition);
                                System.out.println("      - Khoảng thời gian: " + start + " - " + end);
                            } catch (NumberFormatException e) {
                                System.err.println("❌ Lỗi định dạng số ở dòng " + lineCount + ": " + line);
                            }
                        } else {
                            System.err.println("❌ Dòng không đủ thông tin: " + line);
                        }
                    }
                }

                System.out.println("✅ Tổng số từ đã phân tích: " + wordStressList.size());
            } catch (IOException e) {
                System.err.println("❌ Lỗi đọc file: " + e.getMessage());
                throw e;
            }

            results.put("wordStressDetails", wordStressList);
            return results;
        }

        private Map<String, Double> runPraatAnalysis(File wavFile, File textGridFile) throws IOException, InterruptedException {
            File outputFile = File.createTempFile("praat-output", ".txt");
            System.out.println("▶️ [PRAAT ANALYSIS] Starting analysis...");
            System.out.println("   Input WAV: " + wavFile.getAbsolutePath());
            System.out.println("   TextGrid: " + textGridFile.getAbsolutePath());
            System.out.println("   Output will be saved to: " + outputFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run", PRAAT_SCRIPT_PATH,
                    wavFile.getAbsolutePath(),
                    textGridFile.getAbsolutePath(),
                    outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);  // Gom stderr và stdout vào cùng 1 stream
            System.out.println("⚡ [PRAAT] Command: " + String.join(" ", pb.command()));

            Process process = pb.start();
            System.out.println("🔍 [PRAAT OUTPUT] Real-time output:");

            // Đọc toàn bộ stdout/stderr của Praat ra console
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Praat] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("✅ [PRAAT] Process exited with code: " + exitCode);

            if (exitCode != 0) {
                System.err.println("❌ [ERROR] Praat analysis failed!");
                throw new RuntimeException("Praat process failed with exit code: " + exitCode);
            }

            System.out.println("📊 [PRAAT] Parsing results from: " + outputFile.getAbsolutePath());
            System.out.println("✅ Praat process hoàn tất. Bắt đầu đọc file output...");

            return parsePraatOutput(outputFile);
        }


        private Map<String, Double> parsePraatOutput(File outputFile) throws IOException {
                Map<String, Double> results = new HashMap<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            results.put(parts[0], Double.parseDouble(parts[1]));
                        }
                    }
                }
                return results;
            }


        private File downloadAudioFile(String url) throws IOException {
            System.out.println("Đang tải file từ URL: " + url);
            File file = Files.createTempFile("prosody-", ".mp3").toFile();
            try (InputStream in = new URL(url).openStream(); OutputStream out = new FileOutputStream(file)) {
                in.transferTo(out);
            }
            System.out.println("Tải file thành công: " + file.getAbsolutePath());
            return file;
        }

        private File convertMp3ToWav(File mp3File) throws IOException, InterruptedException {
            System.out.println("Bắt đầu chuyển đổi MP3 sang WAV...");
            File wavFile = new File(mp3File.getParent(), mp3File.getName().replace(".mp3", ".wav"));
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y", "-i", mp3File.getAbsolutePath(),
                    "-ar", "44100", "-ac", "1", wavFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg conversion failed with code: " + exitCode);
            }
            System.out.println("Chuyển đổi thành công sang WAV: " + wavFile.getAbsolutePath());
            return wavFile;
        }
    }