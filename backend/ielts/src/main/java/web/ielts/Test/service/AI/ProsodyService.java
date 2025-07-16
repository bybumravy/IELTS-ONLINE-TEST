    package web.ielts.Test.service.AI;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;

    import java.io.*;
    import java.net.URL;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.time.Instant;
    import java.util.*;


    @Service
    public class ProsodyService {

            private final String PRAAT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\praat6438_win-intel64\\Praat.exe";
            private final String PRAAT_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script.praat"; // Script Praat
            private final String STRESS_ANALYSIS_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\stressAnalysis.praat";
            private final String INTONATION_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script_intonation.praat";
            private final String CMU_DICT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\cmudict-0.7b.txt";
            @Value("${openai.api.key}")
            private String openaiApiKey;
            private final RestTemplate restTemplate = new RestTemplate();
            private final ObjectMapper objectMapper = new ObjectMapper();
        private final List<String> stressMismatches = new ArrayList<>();

        private final Map<String, String> cmuDictMap = new HashMap<>(); // Lưu trữ CMU Dict

        public ProsodyService() {
            loadCmuDict();
        }

        // Phương thức đọc CMU Dict từ file
        private void loadCmuDict() {
            File cmuDictFile = new File(CMU_DICT_PATH);
            System.out.println("Loading CMU Dict from: " + CMU_DICT_PATH);
            Path path = Paths.get(CMU_DICT_PATH);
            if (!Files.exists(path)) {
                System.err.println("❌ File not found: " + CMU_DICT_PATH);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(cmuDictFile))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(";;;")) continue; // Bỏ qua comment

                    String[] parts = line.split("\\s+", 2);
                    if (parts.length < 2) continue;

                    String word = parts[0].toLowerCase().replaceAll("[^a-z]", "");
                    String pronunciation = parts[1];

                    if (!word.isEmpty()) {
                        cmuDictMap.put(word, pronunciation);
                        count++;
                    }
                }
                System.out.println("Loaded " + count + " words from CMU Dict");
            } catch (IOException e) {
                System.err.println("Error loading CMU Dict: " + e.getMessage());
            }
        }

        private Map<String, Object> evaluatePronunciation(
                String transcript,
                String intonationAnalysis
        ) {
            System.out.println("\n🔊 [PRONUNCIATION] Starting AI evaluation...");

            try {

                // 2. Xây dựng prompt chi tiết với dữ liệu đã parse
                String prompt = buildPronunciationPrompt(transcript, intonationAnalysis);

                // 3. Gọi API OpenAI
                String aiResponse = callOpenAIPronunciation(prompt);
                System.out.println(aiResponse);
                // 4. Parse và trả về kết quả
                return parsePronunciationResponse(aiResponse);
            } catch (Exception e) {
                System.err.println("❌ Error in pronunciation evaluation: " + e.getMessage());
                e.printStackTrace();
                return Map.of(
                        "error", "Pronunciation evaluation failed",
                        "details", e.getMessage()
                );
            }
        }

        private String buildPronunciationPrompt(
                String transcript,
                String intonationAnalysis
        ) {
            StringBuilder prompt = new StringBuilder();

            prompt.append("You are a certified IELTS Speaking examiner and an expert in English intonation.\n")
                    .append("Your task is to identify important words in the given transcript that SHOULD be emphasized for natural and effective intonation, but are NOT marked as emphasized in the provided intonation analysis.\n\n");

            prompt.append("=== Instructions ===\n")
                    .append("- Carefully read the transcript and its sentences.\n")
                    .append("- Compare each sentence and its emphasized words listed in the intonation analysis.\n")
                    .append("- Identify words that are **semantically important** (such as names, cities, countries, actions, contrastive markers, new information, etc.).\n")
                    .append("- Pay special attention to:\n")
                    .append("  * Proper nouns (names of people, places, etc.)\n")
                    .append("  * Main action verbs\n")
                    .append("  * Contrastive or emphatic elements\n")
                    .append("- Do NOT include function words (e.g., the, and, of, to) or already-emphasized words.\n")
                    .append("- Return ONLY a JSON array, where each element is an object with two fields: 'text' (the word) and 'sentenceText' (the full sentence containing that word).\n")
                    .append("- Do NOT return any explanation, comments, or extra text.\n\n");

            prompt.append("=== Example ===\n")
                    .append("Transcript: Last year I traveled to Japan and visited Kyoto, Tokyo, and Osaka.\n")
                    .append("Intonation Analysis:\n")
                    .append("    Emphasized word: 'Last'\n")
                    .append("    Emphasized word: 'year'\n")
                    .append("    Emphasized word: 'traveled'\n")
                    .append("    Emphasized word: 'Japan'\n")
                    .append("Result:\n")
                    .append("[")
                    .append("{\"text\": \"Kyoto\", \"sentenceText\": \"Last year I traveled to Japan and visited Kyoto, Tokyo, and Osaka.\"},\n")
                    .append("{\"text\": \"Tokyo\", \"sentenceText\": \"Last year I traveled to Japan and visited Kyoto, Tokyo, and Osaka.\"},\n")
                    .append("{\"text\": \"Osaka\", \"sentenceText\": \"Last year I traveled to Japan and visited Kyoto, Tokyo, and Osaka.\"},\n")
                    .append("{\"text\": \"visited\", \"sentenceText\": \"Last year I traveled to Japan and visited Kyoto, Tokyo, and Osaka.\"}\n")
                    .append("]\n\n");

            prompt.append("=== Transcript ===\n")
                    .append(transcript)
                    .append("\n\n");

            prompt.append("=== Intonation Analysis ===\n")
                    .append(intonationAnalysis)
                    .append("\n\n");

            prompt.append("Now, return ONLY the missing emphasized words in the specified JSON array format. Do not repeat words that are already emphasized. Do not return any explanation or extra text.\n");

            System.out.println(prompt.toString());
            return prompt.toString();
        }


        private String callOpenAIPronunciation(String prompt) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(openaiApiKey);

                Map<String, Object> requestBody = Map.of(
                        "model", "gpt-4o",
                        "messages", List.of(
                                Map.of("role", "system", "content", "You are an IELTS pronunciation expert."),
                                Map.of("role", "user", "content", prompt)
                        ),
                        "temperature", 0.2
                );

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://api.openai.com/v1/chat/completions",
                        entity,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("📝 Raw AI Response:");
                    System.out.println(response.getBody());

                    JsonNode root = objectMapper.readTree(response.getBody());
                    return root.path("choices").get(0).path("message").path("content").asText();
                }
                else {
                    throw new RuntimeException("OpenAI API error: " + response.getStatusCode());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to call OpenAI API", e);
            }
        }


        private Map<String, Object> parsePronunciationResponse(String aiResponse) {
            try {
                // Tìm JSON trong response
                int startIdx = aiResponse.indexOf("{");
                int endIdx = aiResponse.lastIndexOf("}");

                if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                    String jsonStr = aiResponse.substring(startIdx, endIdx + 1);

                    // Log JSON trước khi parse
                    System.out.println("\n===== JSON TO PARSE =====");
                    System.out.println(jsonStr);
                    System.out.println("========================\n");

                    return objectMapper.readValue(jsonStr, Map.class);
                } else {
                    String errorMsg = "No JSON found in AI response: " + aiResponse;
                    System.err.println(errorMsg);
                    return Map.of("error", errorMsg);
                }
            } catch (Exception e) {
                String errorMsg = "Error parsing pronunciation response: " + e.getMessage();
                System.err.println(errorMsg);
                return Map.of("error", errorMsg);
            }
        }
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
            stressMismatches.clear();
            List<Map<String, Object>> stressMismatchesDetailed = new ArrayList<>();
            List<Map<String, Object>> pronunciationEvaluationList = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();
            try {
                // 1. Tải và chuyển đổi file âm thanh
                File mp3File = downloadAudioFile(audioUrl);
                File wavFile = convertMp3ToWav(mp3File);

                // 2. Tạo TextGrid
                File textGridFile = generateTextGridFromJson(root, wavFile);

                // 3. Phân tích prosody cơ bản
                System.out.println("Thong so co ban");
                String praatResults = runPraatAnalysis(wavFile, textGridFile);

                // 4. Phân tích trọng âm từ (CHI TIẾT VỊ TRÍ)
                System.out.println("Trong am");
                List<WordInfo> wordInfoList = new ArrayList<>();
                if (root.has("words")) {
                    int idx = 0;
                    for (JsonNode wordNode : root.get("words")) {
                        String w = wordNode.get("word").asText().toLowerCase();
                        double start = wordNode.has("start") ? wordNode.get("start").asDouble() : -1;
                        double end = wordNode.has("end") ? wordNode.get("end").asDouble() : -1;
                        wordInfoList.add(new WordInfo(w, start, end, idx));
                        idx++;
                    }
                } else if (root.has("segments")) {
                    int idx = 0;
                    for (JsonNode segment : root.get("segments")) {
                        for (JsonNode wordNode : segment.get("words")) {
                            String w = wordNode.get("word").asText().toLowerCase();
                            double start = wordNode.has("start") ? wordNode.get("start").asDouble() : -1;
                            double end = wordNode.has("end") ? wordNode.get("end").asDouble() : -1;
                            wordInfoList.add(new WordInfo(w, start, end, idx));
                            idx++;
                        }
                    }
                }
                List<DetectedStressWord> detectedStressWords = new ArrayList<>();
                String stressResults = parseStressOutputWithList(textGridFile, detectedStressWords);
                // Compare and build detailed mismatches by index
                for (int i = 0; i < detectedStressWords.size(); i++) {
                    DetectedStressWord detected = detectedStressWords.get(i);
                    if (i >= wordInfoList.size()) break; // safety
                    WordInfo info = wordInfoList.get(i);
                    Integer standardPosition = getStandardStressPosition(detected.word);
                    if (standardPosition == null || !standardPosition.equals(detected.detectedPosition)) {
                        Map<String, Object> detail = new HashMap<>();
                        detail.put("word", detected.word);
                        detail.put("detectedPosition", detected.detectedPosition);
                        detail.put("standardPosition", standardPosition);
                        detail.put("start", info.start);
                        detail.put("end", info.end);
                        detail.put("index", info.index);
                        stressMismatchesDetailed.add(detail);
                    }
                }
                System.out.println("\n===== STRESS COMPARISON RESULTS =====");
                if (stressMismatchesDetailed.isEmpty()) {
                    System.out.println("All words match CMU Dictionary stress patterns");
                } else {
                    System.out.println("Words with stress mismatches:");
                    for (Map<String, Object> mismatch : stressMismatchesDetailed) {
                        System.out.println("  - " + mismatch);
                    }
                }
                System.out.println("=====================================");
                // 5. Phân tích ngữ điệu câu (MỚI)
                System.out.println("Ngu dieu cau");
                String intonationResults = analyzeSentenceIntonation(wavFile, textGridFile);

                // 6. Tính điểm tổng hợp

                // 7. Tổng hợp kết quả
                // 8. Phân tích và chấm điểm Pronunciation bằng AI
                if (root.has("text")) {
                    String transcript = root.get("text").asText();
                    Map<String, Object> pronunciationEvaluation = evaluatePronunciation(
                            transcript,
                            intonationResults
                    );
                    // Nếu kết quả là Map, chuyển thành list chứa 1 object
                    if (pronunciationEvaluation != null) {
                        if (pronunciationEvaluation instanceof Map) {
                            pronunciationEvaluationList.add(pronunciationEvaluation);
                        }
                    }
                }
                result.put("stressMismatchesDetailed", stressMismatchesDetailed);
                result.put("pronunciationEvaluation", pronunciationEvaluationList);
                System.out.println("\n=======================================");
                System.out.println("🎉 ANALYSIS COMPLETED SUCCESSFULLY");
                System.out.println("   Final result: " + result);
                System.out.println("=======================================");
                return result;
            } catch (Exception e) {
                System.err.println("❌ ANALYSIS ERROR: " + e.getMessage());
                e.printStackTrace();
                Map<String, Object> errorObj = new HashMap<>();
                errorObj.put("error", e.getMessage());
                errorObj.put("stackTrace", Arrays.toString(e.getStackTrace()));
                // Trả về cả hai trường đều là list chứa 1 object lỗi
                List<Map<String, Object>> errorList = new ArrayList<>();
                errorList.add(errorObj);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("stressMismatchesDetailed", errorList);
                errorResult.put("pronunciationEvaluation", errorList);
                return errorResult;
            }
        }

        // Helper class for word info
        private static class WordInfo {
            String word;
            double start;
            double end;
            int index;
            WordInfo(String word, double start, double end, int index) {
                this.word = word;
                this.start = start;
                this.end = end;
                this.index = index;
            }
        }
        // Helper class for detected stress word
        private static class DetectedStressWord {
            String word;
            int detectedPosition;
            DetectedStressWord(String word, int detectedPosition) {
                this.word = word;
                this.detectedPosition = detectedPosition;
            }
        }
        // Helper: like parseStressOutput but fills detectedStressWords list in order
        private String parseStressOutputWithList(File textGridFile, List<DetectedStressWord> detectedStressWords) throws IOException, InterruptedException {
            File outputFile = new File(textGridFile.getParent(),
                    "stress_output_" + Instant.now().toEpochMilli() + ".txt");
            ProcessBuilder pb = new ProcessBuilder(
                    PRAAT_PATH, "--run",
                    STRESS_ANALYSIS_SCRIPT_PATH,
                    textGridFile.getAbsolutePath().replace(".TextGrid", ".wav"),
                    textGridFile.getAbsolutePath(),
                    outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (reader.readLine() != null) {}
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Stress analysis failed with code: " + exitCode);
            }
            StringBuilder resultBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    resultBuilder.append(line).append("\n");
                    if (line.contains(":")) {
                        String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            String word = parts[0].trim().toLowerCase();
                            String positionStr = parts[1].trim().replaceAll("[^0-9]", "");
                            if (!positionStr.isEmpty()) {
                                try {
                                    int detectedPosition = Integer.parseInt(positionStr);
                                    detectedStressWords.add(new DetectedStressWord(word, detectedPosition));
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing stress position: " + line);
                                }
                            }
                        }
                    }
                }
            }
            return resultBuilder.toString();
        }

        private String analyzeWordStress(File wavFile, File textGridFile)
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


        private String analyzeSentenceIntonation(File wavFile, File textGridFile) {
            Map<String, Object> results = new HashMap<>();
            List<Map<String, Object>> sentencesInfo = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

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
                System.out.println("🔊 [INTONATION] Running command: " + String.join(" ", pb.command()));
                pb.redirectErrorStream(true);

                Process process = pb.start();

                // Đọc và log output của Praat
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
                if (exitCode != 0) {
                    throw new IOException("Intonation analysis failed with exit code: " + exitCode);
                }

                // Parse kết quả đầu ra
                System.out.println("📊 [INTONATION] Parsing results from: " + outputFile.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                    String line;
                    Map<String, Object> currentSentence = null;
                    List<Map<String, Object>> emphasizedWords = new ArrayList<>();

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Sentence ")) {
                            // Kết thúc câu trước đó nếu có
                            if (currentSentence != null) {
                                currentSentence.put("emphasizedWords", emphasizedWords);
                                sentencesInfo.add(currentSentence);
                                emphasizedWords = new ArrayList<>();
                            }

                            // Bắt đầu câu mới
                            currentSentence = new HashMap<>();
                            String[] parts = line.split("\\|");

                            // Xử lý thông tin câu
                            String sentenceNum = parts[0].replace("Sentence", "").trim();
                            currentSentence.put("sentenceNumber", Integer.parseInt(sentenceNum));

                            String startTime = parts[1].replace("Start:", "").replace("s", "").trim();
                            currentSentence.put("start", Double.parseDouble(startTime));

                            String endTime = parts[2].replace("End:", "").replace("s", "").trim();
                            currentSentence.put("end", Double.parseDouble(endTime));

                            String meanIntensity = parts[3].replace("Mean Intensity:", "").replace("dB", "").trim();
                            currentSentence.put("meanIntensity", Double.parseDouble(meanIntensity));

                        } else if (line.contains("Emphasized word:")) {
                            // Xử lý từ được nhấn mạnh
                            String word = line.split("'")[1];
                            String intensity = line.split("\\|")[1].replace("dB", "").trim();

                            Map<String, Object> wordInfo = new HashMap<>();
                            wordInfo.put("word", word);
                            wordInfo.put("intensity", Double.parseDouble(intensity));
                            emphasizedWords.add(wordInfo);
                        }
                    }

                    // Thêm câu cuối cùng
                    if (currentSentence != null) {
                        currentSentence.put("emphasizedWords", emphasizedWords);
                        sentencesInfo.add(currentSentence);
                    }
                }

                results.put("sentences", sentencesInfo);
                results.put("analysisSuccess", true);
                results.put("totalSentences", sentencesInfo.size());

            } catch (Exception e) {
                System.err.println("💥 Sentence intonation analysis error: " + e.getMessage());
                e.printStackTrace();
                results.put("error", e.getMessage());
                results.put("analysisSuccess", false);
            }

            try {
                // Chuyển Map sang JSON string
                String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);

                // In JSON ra terminal
                System.out.println("📤 JSON Result:\n" + jsonResult);

                // Trả về JSON string
                return jsonResult;

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"error\":\"Failed to convert result to JSON.\"}";
            }

        }

        private String parseStressOutput(File outputFile) throws IOException {
            StringBuilder resultBuilder = new StringBuilder();
            Map<String, Integer> detectedStressMap = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    resultBuilder.append(line).append("\n");

                    // Phân tích dòng kết quả: định dạng "word: position"
                    if (line.contains(":")) {
                        String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            String word = parts[0].trim();
                            String positionStr = parts[1].trim().replaceAll("[^0-9]", "");

                            if (!positionStr.isEmpty()) {
                                try {
                                    int detectedPosition = Integer.parseInt(positionStr);
                                    detectedStressMap.put(word.toLowerCase(), detectedPosition);
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing stress position: " + line);
                                }
                            }
                        }
                    }
                }
            }

            // So sánh với CMU Dictionary
            compareWithCmuDict(detectedStressMap);

            return resultBuilder.toString();
        }
        private void compareWithCmuDict(Map<String, Integer> detectedStressMap) {
            for (Map.Entry<String, Integer> entry : detectedStressMap.entrySet()) {
                String word = entry.getKey();
                int detectedPosition = entry.getValue();

                // Lấy trọng âm chuẩn từ CMU Dict
                Integer standardPosition = getStandardStressPosition(word);

                if (standardPosition == null) {
                    stressMismatches.add(word + ": Not found in CMU Dictionary");
                } else if (standardPosition != detectedPosition) {
                    stressMismatches.add(word + ": Detected=" + detectedPosition +
                            ", Standard=" + standardPosition);
                }
            }
        }
        private Integer getStandardStressPosition(String word) {
            String cleanWord = word.toLowerCase().replaceAll("[^a-z]", "");
            String pronunciation = cmuDictMap.get(cleanWord);

            if (pronunciation == null) {
                return null;
            }

            // Chỉ lọc các âm tiết chứa nguyên âm (có số)
            String[] syllables = pronunciation.split("\\s+");
            List<String> vowelSyllables = new ArrayList<>();
            for (String syl : syllables) {
                if (syl.matches(".*[0-2]$")) {
                    vowelSyllables.add(syl);
                }
            }

            int stressPosition = 0;
            int syllableCount = vowelSyllables.size();

            // Xác định vị trí trọng âm
            if (syllableCount == 1) {
                stressPosition = 1;
            } else {
                for (int i = 0; i < syllableCount; i++) {
                    if (vowelSyllables.get(i).contains("1")) {
                        stressPosition = i + 1;
                        break;
                    }
                }
            }

            return stressPosition > 0 ? stressPosition : null;
        }


        private String runPraatAnalysis(File wavFile, File textGridFile) throws IOException, InterruptedException {
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
            pb.redirectErrorStream(true);
            System.out.println("⚡ [PRAAT] Command: " + String.join(" ", pb.command()));

            Process process = pb.start();
            System.out.println("🔍 [PRAAT OUTPUT] Real-time output:");

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

            // Trả về JSON string từ parsePraatOutput
            return parsePraatOutput(outputFile);
        }



        private String parsePraatOutput(File outputFile) throws IOException {
            Map<String, Double> results = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("=")) continue;

                    String[] parts = line.split("=");
                    if (parts.length != 2) continue;

                    String key = parts[0].trim();
                    String valueStr = parts[1].trim();

                    try {
                        double value = Double.parseDouble(valueStr);
                        results.put(key, value);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Không thể parse value: '" + valueStr + "' cho key: " + key);
                    }
                }
            }

            // Chuyển Map sang JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);

            // In JSON ra terminal
            System.out.println("📤 JSON Parsed Result:\n" + jsonResult);

            return jsonResult;
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