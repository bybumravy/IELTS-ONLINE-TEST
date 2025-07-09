    package web.ielts.Test.service.AI;
    import com.fasterxml.jackson.databind.JsonNode;
    import org.springframework.stereotype.Service;

    import java.io.*;
    import java.net.URL;
    import java.nio.file.Files;
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

        public Map<String, Object> analyzePronunciation(String audioUrl, JsonNode root) {
            Map<String, Object> result = new HashMap<>();
            try {
                // 1. Download and convert audio
                File mp3File = downloadAudioFile(audioUrl);
                File wavFile = convertMp3ToWav(mp3File);

                // 2. Generate TextGrid from transcript
                File textGridFile = generateTextGridFromJson(root, wavFile);

                // 3. Run Praat analysis
                Map<String, Double> praatResults = runPraatAnalysis(wavFile, textGridFile);

                // 4. Calculate pronunciation score
                double pronunciationScore = calculatePronunciationScore(praatResults);

                // 5. Prepare results
                result.put("pronunciationScore", pronunciationScore);
                result.putAll(praatResults);

                System.out.println("📊 Pronunciation analysis results:");
                result.forEach((key, value) -> System.out.println(key + ": " + value));

                return result;
            } catch (Exception e) {
                return Map.of("error", e.getMessage());
            }
        }

        private double calculatePronunciationScore(Map<String, Double> praatResults) {
            // Các trọng số cho từng tiêu chí
            final double WEIGHT_PAUSE = 0.15;
            final double WEIGHT_SPEECH_RATE = 0.15;
            final double WEIGHT_PITCH_VARIABILITY = 0.25;
            final double WEIGHT_INTENSITY_VARIABILITY = 0.25;
            final double WEIGHT_CONNECTED_SPEECH = 0.20;

            // Tính điểm cho từng tiêu chí (thang điểm 0-9)
            double pauseScore = calculatePauseScore(
                    praatResults.get("pauseCount"),
                    praatResults.get("totalPauseDuration"),
                    praatResults.get("speakingDuration")
            );

            double speechRateScore = calculateSpeechRateScore(
                    praatResults.get("speechRate")
            );

            double pitchVariabilityScore = calculatePitchVariabilityScore(
                    praatResults.get("pitchSD")
            );

            double intensityVariabilityScore = calculateIntensityVariabilityScore(
                    praatResults.get("intensitySD")
            );

            double connectedSpeechScore = calculateConnectedSpeechScore(
                    praatResults.get("totalPauseDuration"),
                    praatResults.get("speakingDuration")
            );

            // Tổng hợp điểm tổng
            return (pauseScore * WEIGHT_PAUSE) +
                    (speechRateScore * WEIGHT_SPEECH_RATE) +
                    (pitchVariabilityScore * WEIGHT_PITCH_VARIABILITY) +
                    (intensityVariabilityScore * WEIGHT_INTENSITY_VARIABILITY) +
                    (connectedSpeechScore * WEIGHT_CONNECTED_SPEECH);
        }

        private double calculatePauseScore(double pauseCount, double totalPauseDuration, double speakingDuration) {
            double pauseRatio = totalPauseDuration / speakingDuration;

            // Band 9: Tạm dừng tối thiểu (<5% thời gian nói)
            if (pauseRatio < 0.05 && pauseCount < 3) return 9.0;
            // Band 8: Tạm dừng ít (5-10%)
            if (pauseRatio < 0.10 && pauseCount < 5) return 8.0;
            // Band 7: Tạm dừng vừa phải (10-15%)
            if (pauseRatio < 0.15) return 7.0;
            // Band 6: Tạm dừng nhiều (15-20%)
            if (pauseRatio < 0.20) return 6.0;
            // Band 5: Tạm dừng quá nhiều (>20%)
            return 5.0;
        }

        private double calculateSpeechRateScore(double speechRate) {
            // Band 9: Tốc độ tự nhiên (140-160 wpm)
            if (speechRate >= 140 && speechRate <= 160) return 9.0;
            // Band 8: Tốc độ tốt (130-140 hoặc 160-170 wpm)
            if ((speechRate >= 130 && speechRate < 140) || (speechRate > 160 && speechRate <= 170)) return 8.0;
            // Band 7: Tốc độ chấp nhận được (120-130 hoặc 170-180 wpm)
            if ((speechRate >= 120 && speechRate < 130) || (speechRate > 170 && speechRate <= 180)) return 7.0;
            // Band 6: Tốc độ không đều (110-120 hoặc 180-190 wpm)
            if ((speechRate >= 110 && speechRate < 120) || (speechRate > 180 && speechRate <= 190)) return 6.0;
            // Band 5: Tốc độ quá chậm/nhanh (<110 hoặc >190 wpm)
            return 5.0;
        }

        private double calculatePitchVariabilityScore(double pitchSD) {
            // Band 9: Ngữ điệu tự nhiên (SD > 25 Hz)
            if (pitchSD > 25) return 9.0;
            // Band 8: Ngữ điệu tốt (20-25 Hz)
            if (pitchSD >= 20) return 8.0;
            // Band 7: Ngữ điệu chấp nhận được (15-20 Hz)
            if (pitchSD >= 15) return 7.0;
            // Band 6: Ngữ điệu hạn chế (10-15 Hz)
            if (pitchSD >= 10) return 6.0;
            // Band 5: Ngữ điệu đơn điệu (<10 Hz)
            return 5.0;
        }

        private double calculateIntensityVariabilityScore(double intensitySD) {
            // Band 9: Nhấn âm xuất sắc (SD > 8 dB)
            if (intensitySD > 8) return 9.0;
            // Band 8: Nhấn âm tốt (6-8 dB)
            if (intensitySD >= 6) return 8.0;
            // Band 7: Nhấn âm chấp nhận được (4-6 dB)
            if (intensitySD >= 4) return 7.0;
            // Band 6: Nhấn âm hạn chế (2-4 dB)
            if (intensitySD >= 2) return 6.0;
            // Band 5: Nhấn âm kém (<2 dB)
            return 5.0;
        }

        private double calculateConnectedSpeechScore(double totalPauseDuration, double speakingDuration) {
            double speechRatio = speakingDuration / (speakingDuration + totalPauseDuration);

            // Band 9: Nối âm liền mạch (>95% thời gian nói)
            if (speechRatio > 0.95) return 9.0;
            // Band 8: Nối âm tốt (90-95%)
            if (speechRatio > 0.90) return 8.0;
            // Band 7: Nối âm chấp nhận được (85-90%)
            if (speechRatio > 0.85) return 7.0;
            // Band 6: Nối âm hạn chế (80-85%)
            if (speechRatio > 0.80) return 6.0;
            // Band 5: Nối âm kém (<80%)
            return 5.0;
        }

        private File generateTextGridFromJson(JsonNode root, File audioFile) throws IOException {
            File textGridFile = new File(audioFile.getParent(), audioFile.getName().replace(".wav", ".TextGrid"));
            System.out.println("👉 Bắt đầu generate TextGrid file: " + textGridFile.getAbsolutePath());
            try (PrintWriter writer = new PrintWriter(textGridFile)) {
                double audioDuration;
                try {
                    audioDuration = praatGetAudioDuration(audioFile); // helper lấy duration file wav
                    System.out.println("✅ Audio duration: " + audioDuration);
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi lấy audio duration: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }

                writer.println("File type = \"ooTextFile\"");
                writer.println("Object class = \"TextGrid\"");
                writer.println();
                writer.println("xmin = 0");
                writer.println("xmax = " + audioDuration);
                writer.println("tiers? <exists>");
                writer.println("size = 1");
                writer.println("item []:");
                writer.println("    item [1]:");
                writer.println("        class = \"IntervalTier\"");
                writer.println("        name = \"words\"");
                writer.println("        xmin = 0");
                writer.println("        xmax = " + audioDuration);

                List<JsonNode> wordNodes = new ArrayList<>();
                try {
                    if (root.has("segments")) {
                        root.get("segments").forEach(segment ->
                                segment.get("words").forEach(wordNodes::add)
                        );
                    } else if (root.has("words")) {
                        root.get("words").forEach(wordNodes::add);
                    }
                    System.out.println("✅ Tổng số từ: " + wordNodes.size());
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi đọc JSON words/segments: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }

                writer.println("        intervals: size = " + wordNodes.size());

                for (int i = 0; i < wordNodes.size(); i++) {
                    JsonNode word = wordNodes.get(i);
                    try {
                        double start = word.get("start").asDouble();
                        double end = word.get("end").asDouble();
                        String wordText = word.get("word").asText();
                        writer.println("        intervals [" + (i + 1) + "]:");
                        writer.println("            xmin = " + start);
                        writer.println("            xmax = " + end);
                        writer.println("            text = \"" + wordText + "\"");
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi xử lý word tại index " + i + ": " + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                }

                System.out.println("✅ Ghi file TextGrid thành công: " + textGridFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("❌ IOException khi ghi TextGrid: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            return textGridFile;
        }


        public Map<String, Object> analyze(String audioUrl, JsonNode root) {
                Map<String, Object> result = new HashMap<>();
                try {
                    // 1. Tải file audio và chuyển đổi sang WAV (giữ nguyên)
                    File mp3File = downloadAudioFile(audioUrl);
                    File wavFile = convertMp3ToWav(mp3File);

                    // 2. Gọi Praat để phân tích
                    File textGridFile = generateTextGridFromJson(root, wavFile);
                    // 1. Basic prosody analysis
                    Map<String, Double> praatResults = runPraatAnalysis(wavFile, textGridFile);

                    // 2. Stress analysis
                    Map<String, Object> stressResults = analyzeWordStress(wavFile, textGridFile, root);

                    // 3. Pronunciation scoring
                    Map<String, Object> pronunciationScore = evaluatePronunciation(praatResults, stressResults);

                    result.putAll(pronunciationScore);
                    result.put("detailedStress", stressResults.get("wordStress"));
                    result.put("prosodyFeatures", praatResults);

                    return result;
                } catch (Exception e) {
                    return Map.of("error", e.getMessage());
                }
            }

        private double calculateIntonationScore(Map<String, Double> praatResults) {
            double pitchRange = praatResults.get("maxPitch") - praatResults.get("minPitch");
            double optimalRange = 100; // Hz

            // Score based on how close the pitch range is to optimal
            double rangeScore = 1 - Math.min(1, Math.abs(pitchRange - optimalRange) / optimalRange);

            // Also consider pitch variability (standard deviation would be better)
            double variabilityScore = praatResults.get("meanPitch") > 0 ? 0.7 : 0.5;

            return (rangeScore * 0.6 + variabilityScore * 0.4);
        }
        private Map<String, Object> evaluatePronunciation(Map<String, Double> praatResults, Map<String, Object> stressResults) {
            Map<String, Object> evaluation = new HashMap<>();

            // 1. Phoneme accuracy (simplified - would need phoneme-level analysis)
            evaluation.put("phonemeAccuracy", 0.85); // Placeholder

            // 2. Word stress
            evaluation.put("wordStressScore", stressResults.get("stressAccuracy"));

            // 3. Sentence stress and intonation
            double intonationScore = calculateIntonationScore(praatResults);
            evaluation.put("intonationScore", intonationScore);

            // 4. Connected speech features
            evaluation.put("connectedSpeechScore", 0.8); // Placeholder

            // 5. Overall comprehensibility
            double overallScore = (
                    (double) evaluation.get("phonemeAccuracy") * 0.3 +
                            (double) evaluation.get("wordStressScore") * 0.25 +
                            (double) evaluation.get("intonationScore") * 0.25 +
                            (double) evaluation.get("connectedSpeechScore") * 0.2
            );

            // Convert to IELTS band score (simplified mapping)
            double bandScore = overallScore * 4 + 1; // Maps 0-1 to 1-5 scale
            if (bandScore > 9) bandScore = 9;

            evaluation.put("pronunciationBandScore", Math.round(bandScore * 2) / 2.0); // Round to nearest 0.5

            return evaluation;
        }


        private Map<String, Object> analyzeWordStress(File wavFile, File textGridFile, JsonNode root) throws IOException, InterruptedException {
            File outputFile = File.createTempFile("stress-output", ".txt");

            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run", STRESS_ANALYSIS_SCRIPT_PATH,
                    wavFile.getAbsolutePath(),
                    textGridFile.getAbsolutePath(),
                    outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Stress analysis failed with exit code: " + exitCode);
            }

            return parseStressOutput(outputFile, root);
        }

        private Map<String, Object> parseStressOutput(File outputFile, JsonNode root) throws IOException {
            Map<String, Object> results = new HashMap<>();
            List<Map<String, Object>> wordStressList = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("WORD_STRESS")) {
                        String[] parts = line.split(":");
                        if (parts.length == 3) {
                            String word = parts[1].trim();
                            double stressLevel = Double.parseDouble(parts[2].trim());

                            Map<String, Object> wordStress = new HashMap<>();
                            wordStress.put("word", word);
                            wordStress.put("stressLevel", stressLevel);
                            wordStress.put("isStressed", stressLevel > 1.5); // Threshold for stress

                            wordStressList.add(wordStress);
                        }
                    }
                }
            }

            results.put("wordStress", wordStressList);

            // Calculate stress accuracy score
            int correctStresses = 0;
            int totalStressedWords = 0;

            for (Map<String, Object> wordStress : wordStressList) {
                String word = (String) wordStress.get("word");
                boolean isStressed = (boolean) wordStress.get("isStressed");

                // Here you would compare with expected stress patterns
                // This is simplified - in reality you'd need a dictionary of word stresses
                boolean expectedStress = word.length() > 5; // Simple heuristic

                if (isStressed == expectedStress) {
                    correctStresses++;
                }
                if (expectedStress) {
                    totalStressedWords++;
                }
            }

            double stressAccuracy = totalStressedWords > 0 ? (double) correctStresses / totalStressedWords : 1.0;
            results.put("stressAccuracy", stressAccuracy);

            return results;
        }

        private Map<String, Double> runPraatAnalysis(File wavFile, File textGridFile) throws IOException, InterruptedException {
            File outputFile = File.createTempFile("praat-output", ".txt");
            System.out.println("▶️ Output file Praat: " + outputFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run", PRAAT_SCRIPT_PATH,
                    wavFile.getAbsolutePath(),
                    textGridFile.getAbsolutePath(),
                    outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);  // Gom stderr và stdout vào cùng 1 stream

            Process process = pb.start();

            // Đọc toàn bộ stdout/stderr của Praat ra console
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Praat] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Praat process failed with exit code: " + exitCode);
            }

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

        private List<String> analyzeWordEmphasis(JsonNode root, Map<String, Double> praatResults) {
            List<String> emphasizedWords = new ArrayList<>();
            double meanPitch = praatResults.get("meanPitch");
            double meanIntensity = praatResults.get("meanIntensity");

            // 1. Lấy danh sách từ từ transcript (JSON)
            List<JsonNode> wordNodes = new ArrayList<>();
            if (root.has("segments")) {
                root.get("segments").forEach(segment ->
                        segment.get("words").forEach(wordNodes::add)
                );
            } else if (root.has("words")) {
                root.get("words").forEach(wordNodes::add);
            }

            // 2. Phân tích từng từ
            for (JsonNode word : wordNodes) {
                String wordText = word.get("word").asText();
                double start = word.get("start").asDouble();
                double end = word.get("end").asDouble();

                // 3. Giả lập phân tích pitch/intensity cho từ (trong thực tế cần file TextGrid từ Praat)
                // Giả sử từ có pitch cao hơn 1.5x trung bình VÀ intensity > trung bình là được nhấn mạnh
                boolean isEmphasized =
                        (getSimulatedPitchForWord(start, end) > meanPitch * 1.5) &&
                                (getSimulatedIntensityForWord(start, end) > meanIntensity);

                if (isEmphasized) {
                    emphasizedWords.add(wordText);
                }
            }
            return emphasizedWords;
        }

        // Hàm giả lập - Thực tế cần đọc từ file TextGrid kết quả từ Praat
        private double getSimulatedPitchForWord(double start, double end) {
            // Giả lập: từ ở giữa câu có pitch cao hơn
            return 150 + (Math.sin(start) * 50); // Dao động quanh 150Hz
        }

        private double getSimulatedIntensityForWord(double start, double end) {
            // Giả lập: từ đầu/cuối câu có intensity cao hơn
            return 70 + (Math.cos(start) * 10); // Dao động quanh 70dB
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