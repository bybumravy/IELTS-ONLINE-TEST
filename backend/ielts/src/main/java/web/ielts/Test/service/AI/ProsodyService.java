    package web.ielts.Test.service.AI;
    import com.fasterxml.jackson.databind.JsonNode;
    import org.springframework.stereotype.Service;

    import java.io.*;
    import java.net.URL;
    import java.nio.file.Files;
    import java.time.Instant;
    import java.util.*;


    @Service
    public class ProsodyService {

            private final String PRAAT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\praat6438_win-intel64\\Praat.exe";
            private final String PRAAT_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script.praat"; // Script Praat
            private final String STRESS_ANALYSIS_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\stressAnalysis.praat";
        private final String INTONATION_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script_intonation.praat";
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
                writer.println("size = 3"); // Now 3 tiers: words, sentences, syllables
                writer.println("item []:");

                // Word tier
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

                // Sentence tier
                writer.println("    item [2]:");
                writer.println("        class = \"IntervalTier\"");
                writer.println("        name = \"sentences\"");
                writer.println("        xmin = 0");
                writer.println("        xmax = " + audioDuration);

                List<List<JsonNode>> sentences = groupWordsIntoSentences(wordNodes);
                writer.println("        intervals: size = " + sentences.size());

                for (int i = 0; i < sentences.size(); i++) {
                    List<JsonNode> sentenceWords = sentences.get(i);
                    double sentenceStart = sentenceWords.get(0).get("start").asDouble();
                    double sentenceEnd = sentenceWords.get(sentenceWords.size() - 1).get("end").asDouble();

                    writer.println("        intervals [" + (i + 1) + "]:");
                    writer.println("            xmin = " + sentenceStart);
                    writer.println("            xmax = " + sentenceEnd);
                    writer.println("            text = \"Sentence " + (i + 1) + "\"");
                }

                // Syllable tier
                writer.println("    item [3]:");
                writer.println("        class = \"IntervalTier\"");
                writer.println("        name = \"syllables\"");
                writer.println("        xmin = 0");
                writer.println("        xmax = " + audioDuration);
                writer.println("        intervals: size = " + wordNodes.size());

                for (int i = 0; i < wordNodes.size(); i++) {
                    JsonNode word = wordNodes.get(i);
                    double start = word.get("start").asDouble();
                    double end = word.get("end").asDouble();

                    // Lấy syllable count từ JSON
                    String syllableCount = word.has("syllables") ? word.get("syllables").asText() : "1"; // mặc định 1 nếu không có

                    writer.println("        intervals [" + (i + 1) + "]:");
                    writer.println("            xmin = " + start);
                    writer.println("            xmax = " + end);
                    writer.println("            text = \"" + syllableCount + "\"");
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
                Map<String, Object> stressResults = analyzeWordStress(wavFile, textGridFile);

                // 5. Phân tích ngữ điệu câu (MỚI)
                System.out.println("Ngu dieu cau");
                Map<String, Object> intonationResults = analyzeSentenceIntonation(wavFile, textGridFile);

                // 6. Tính điểm tổng hợp

                // 7. Tổng hợp kết quả
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

        private Map<String, Object> analyzeWordStress(File wavFile, File textGridFile)
                throws IOException, InterruptedException {

            // 1. Chuẩn bị output file
            File outputFile = new File(textGridFile.getParent(),
                    "stress_output_" + Instant.now().toEpochMilli() + ".txt");

            System.out.println("\n🎯 [STRESS ANALYSIS] Starting analysis...");
            System.out.println("   WAV File: " + wavFile.getAbsolutePath());
            System.out.println("   TextGrid: " + textGridFile.getAbsolutePath());
            System.out.println("   Output: " + outputFile.getAbsolutePath());

            // 2. Verify input files
            if (!wavFile.exists()) throw new FileNotFoundException("WAV file not found");
            if (!textGridFile.exists()) throw new FileNotFoundException("TextGrid not found");

            // 3. Run Praat process
            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run",
                    STRESS_ANALYSIS_SCRIPT_PATH,
                    wavFile.getAbsolutePath(),
                    textGridFile.getAbsolutePath(),
                    outputFile.getAbsolutePath()
            );

            System.out.println("⚡ Command: " + String.join(" ", pb.command()));

            Process process = pb.start();

            // 4. Capture output streams
            StringBuilder output = new StringBuilder();
            StringBuilder errors = new StringBuilder();

            try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String line;
                while ((line = outReader.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println("[PRAAT] " + line);
                }

                while ((line = errReader.readLine()) != null) {
                    errors.append(line).append("\n");
                    System.err.println("[PRAAT ERROR] " + line);
                }
            }

            // 5. Check process result
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMsg = String.format(
                        "Stress analysis failed (exit code %d)\nErrors:\n%s\nOutput:\n%s",
                        exitCode, errors.toString(), output.toString());
                throw new RuntimeException(errorMsg);
            }

            // 6. Parse results
            if (!outputFile.exists()) {
                throw new IOException("Output file not created: " + outputFile.getAbsolutePath());
            }

            return parseStressOutput(outputFile);
        }


        private Map<String, Object> analyzeSentenceIntonation(File wavFile, File textGridFile) {
            Map<String, Object> results = new HashMap<>();
            List<Map<String, Object>> sentenceIntonations = new ArrayList<>();

            try {
                File outputFile = new File(textGridFile.getParent(),
                        "intonation_output_" + System.currentTimeMillis() + ".txt");

                ProcessBuilder pb = new ProcessBuilder(
                        PRAAT_PATH, "--run",
                        INTONATION_SCRIPT_PATH,
                        wavFile.getAbsolutePath(),
                        textGridFile.getAbsolutePath(),
                        outputFile.getAbsolutePath()
                );
                System.out.println("Running intonation analysis command: " + String.join(" ", pb.command()));
                pb.redirectErrorStream(true);

                Process process = pb.start();

                // Read output for debugging
                StringBuilder praatOutput = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("   [PRAAT] " + line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("Intonation analysis failed with exit code: " + exitCode);
                }

                // Parse the output file with sentence-level results
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                    String line;
                    reader.readLine(); // Bỏ qua header

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 6) {
                            Map<String, Object> sentenceData = new HashMap<>();

                            sentenceData.put("sentenceNumber", parts[0].split(" ")[1]);
                            sentenceData.put("start", Double.parseDouble(parts[1]));
                            sentenceData.put("end", Double.parseDouble(parts[2]));
                            sentenceData.put("pitchStart", Double.parseDouble(parts[3]));
                            sentenceData.put("pitchEnd", Double.parseDouble(parts[4]));
                            sentenceData.put("intonationType", parts[5]);

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

        private Map<String, Object> parseStressOutput(File outputFile) throws IOException {
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