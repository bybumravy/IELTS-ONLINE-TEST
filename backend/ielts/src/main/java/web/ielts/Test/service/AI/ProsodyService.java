    package web.ielts.Test.service.AI;
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;
    import web.ielts.Test.model.answer.speaking.FleCohAnswer;
    import web.ielts.Test.model.answer.speaking.PronunciationAnswer;
    import web.ielts.Test.model.answer.speaking.IntonationSentence;
    import web.ielts.Test.model.answer.speaking.StressMismatch;


    import java.io.*;
    import java.net.URL;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.time.Instant;
    import java.util.*;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;


    @Service
    public class ProsodyService {

        private final String PRAAT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\praat6438_win-intel64\\Praat.exe";
        private final String PRAAT_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script.praat"; // Script Praat
        private final String STRESS_ANALYSIS_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\stressAnalysis.praat";
        private final String INTONATION_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script_intonation.praat";
        private final String CMU_DICT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\cmudict-0.7b.txt";
        private final String GET_DURATION = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\getDuration.praat";
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

        private List<IntonationSentence> parsePronunciationResponseToList(String aiResponse) {
            try {
                Pattern pattern = Pattern.compile("\\[.*?\\]", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(aiResponse);
                if (matcher.find()) {
                    String jsonStr = matcher.group();
                    System.out.println("🔍 Extracted JSON: " + jsonStr);
                    return objectMapper.readValue(
                            jsonStr,
                            new TypeReference<List<IntonationSentence>>() {}
                    );
                } else {
                    System.err.println("❌ No JSON array found in AI response:\n" + aiResponse);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                System.err.println("❌ Error parsing JSON:\n" + e.getMessage());
                return new ArrayList<>();
            }
        }



        private List<IntonationSentence> evaluatePronunciation(
                String transcript
        ) {
            System.out.println("\n🔊 [PRONUNCIATION] Starting AI evaluation...");
            try {
                // 2. Xây dựng prompt chi tiết với dữ liệu đã parse
                String prompt = buildPronunciationPrompt(transcript);

                // 3. Gọi API OpenAI
                String aiResponse = callOpenAIPronunciation(prompt);
                System.out.println(aiResponse);
                // 4. Parse và trả về kết quả
                return parsePronunciationResponseToList(aiResponse);
            } catch (Exception e) {
                System.err.println("❌ Error in pronunciation evaluation: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        private String buildPronunciationPrompt(String transcript) {
            // Tách transcript thành từng từ giống logic mapping index
            List<String> transcriptWords = new ArrayList<>();
            if (transcript != null) {
                for (String word : transcript.split("(\\s+|(?=[,.!?;:]))")) {
                    if (!word.trim().isEmpty()) {
                        transcriptWords.add(word);
                    }
                }
            }

            StringBuilder prompt = new StringBuilder();

            prompt.append("You are a certified IELTS Speaking examiner and an expert in English intonation.\n")
                .append("Your task is to identify ALL important words in the given transcript that SHOULD be emphasized for natural and effective intonation.\n\n")
                .append("=== Instructions ===\n")
                .append("- Carefully read the transcript and its sentences.\n")
                .append("- Below is the tokenized word array from the transcript.\n")
                .append("- Identify words that are semantically important (such as names, cities, countries, actions, contrastive markers, new information, etc.).\n")
                .append("- Pay special attention to:\n")
                .append("  * Proper nouns (names of people, places, etc.)\n")
                .append("  * Main action verbs\n")
                .append("  * Contrastive or emphatic elements\n")
                .append("- Do NOT include function words (e.g., the, and, of, to).\n")
                .append("- Return ONLY a JSON array, where each element is an object with two fields: 'text' (the word) and 'index' (the position of the word in the tokenized array below, starting from 0).\n")
                .append("- The index MUST match the word's order in the tokenized array below.\n")
                .append("- Do NOT return any explanation, comments, or extra text.\n\n")
                .append("=== Example ===\n")
                .append("Transcript: Last year I traveled to Japan and visited Kyoto, Tokyo, and Osaka.\n")
                .append("Tokenized Words:\n")
                .append("[\"Last\", \"year\", \"I\", \"traveled\", \"to\", \"Japan\", \"and\", \"visited\", \"Kyoto\", \"Tokyo\", \"Osaka\", \".\"]\n")
                .append("Result:\n")
                .append("[")
                .append("{\"text\": \"Last\", \"index\": 0},\n")
                .append("{\"text\": \"year\", \"index\": 1},\n")
                .append("{\"text\": \"traveled\", \"index\": 3},\n")
                .append("{\"text\": \"Japan\", \"index\": 5},\n")
                .append("{\"text\": \"Kyoto\", \"index\": 8},\n")
                .append("{\"text\": \"Tokyo\", \"index\": 9},\n")
                .append("{\"text\": \"Osaka\", \"index\": 10},\n")
                .append("{\"text\": \"visited\", \"index\": 7}\n")
                .append("]\n\n")
                .append("=== Transcript ===\n")
                .append(transcript)
                .append("\n\n")
                .append("=== Tokenized Words ===\n")
                .append(transcriptWords)
                .append("\n\n")
                .append("Now, return ONLY the important words in the specified JSON array format. Do not return any explanation or extra text.\n");

            System.out.println(prompt);
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

        private double callOpenAIScorePronunciation(String transcript, List<StressMismatch> stressMismatches, List<IntonationSentence> intonationResults, List<String> importantWords, List<String> emphasizedWords, List<String> correctEmphasizedWords) {
            try {
                StringBuilder prompt = new StringBuilder();
                prompt.append("You are a certified IELTS Speaking examiner.\n")
                    .append("Your task is to give a pronunciation score (from 0 to 9, with 0.5 increments) for the candidate's answer, based on the following information:\n")
                    .append("- The transcript of the answer.\n")
                    .append("- The list of words that SHOULD be emphasized (importantWords, from AI).\n")
                    .append("- The list of words that the student ACTUALLY emphasized (emphasizedWords, from analysis).\n")
                    .append("- The list of words that are BOTH needed and actually emphasized (correctEmphasizedWords).\n")
                    .append("- The list of sentences where the student emphasized correctly (correctEmphasizedSentences).\n")
                    .append("- The list of words that SHOULD be emphasized but were NOT (missingEmphasis).\n")
                    .append("- The list of words that were emphasized but should NOT be (overEmphasis).\n")
                    .append("\n=== Instructions ===\n")
                    .append("- Penalize for frequent or severe stress mismatches, or for missing important intonation/emphasis.\n")
                    .append("- If the answer is mostly correct with minor issues, give a high score (7.5-9).\n")
                    .append("- If there are many errors, give a lower score.\n")
                    .append("- Only return a single number (the score), no explanation, no extra text.\n\n");
                prompt.append("Transcript:\n").append(transcript).append("\n\n");
                prompt.append("Stress mismatches (word, detectedPosition, standardPosition):\n");
                if (stressMismatches == null) stressMismatches = Collections.emptyList();
                for (StressMismatch sm : stressMismatches) {
                    prompt.append(String.format("- %s (detected: %s, standard: %s)\n", sm.getWord(), sm.getDetectedPosition(), sm.getStandardPosition()));
                }
                prompt.append("\nImportant words (should be emphasized):\n");
                for (String w : importantWords) {
                    prompt.append("- ").append(w).append("\n");
                }
                prompt.append("\nEmphasized words (student actually emphasized):\n");
                for (String w : emphasizedWords) {
                    prompt.append("- ").append(w).append("\n");
                }
                prompt.append("\nCorrectly emphasized words (both needed and actually emphasized):\n");
                for (String w : correctEmphasizedWords) {
                    prompt.append("- ").append(w).append("\n");
                }
                // missingEmphasis: cần nhấn mạnh nhưng không nhấn mạnh
                List<String> missingEmphasis = importantWords.stream().filter(w -> emphasizedWords.stream().noneMatch(e -> e.equalsIgnoreCase(w))).collect(Collectors.toList());
                prompt.append("\nMissing emphasis (should be emphasized but were not):\n");
                for (String w : missingEmphasis) {
                    prompt.append("- ").append(w).append("\n");
                }
                // overEmphasis: không cần nhấn mạnh nhưng lại nhấn mạnh
                List<String> overEmphasis = emphasizedWords.stream().filter(w -> importantWords.stream().noneMatch(i -> i.equalsIgnoreCase(w))).collect(Collectors.toList());
                prompt.append("\nOver emphasis (emphasized but should not be):\n");
                for (String w : overEmphasis) {
                    prompt.append("- ").append(w).append("\n");
                }
                prompt.append("\nNow, return ONLY the pronunciation score (0-9, with 0.5 increments). No explanation.\n");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(openaiApiKey);
                Map<String, Object> requestBody = Map.of(
                        "model", "gpt-4o",
                        "messages", List.of(
                                Map.of("role", "system", "content", "You are an IELTS pronunciation expert."),
                                Map.of("role", "user", "content", prompt.toString())
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
                    JsonNode root = objectMapper.readTree(response.getBody());
                    String content = root.path("choices").get(0).path("message").path("content").asText();
                    // Extract the first number (score) from the response
                    Pattern p = Pattern.compile("([0-9]+(\\.[05])?)");
                    Matcher m = p.matcher(content);
                    if (m.find()) {
                        return Double.parseDouble(m.group(1));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting pronunciation score from OpenAI: " + e.getMessage());
            }
            return 0.0; // fallback
        }

            private double praatGetAudioDuration(File wavFile) throws IOException {
            // Lấy đường dẫn tuyệt đối cho script Praat
                String scriptPath = new File(GET_DURATION).getAbsolutePath();
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

                System.out.println("Ghi file TextGrid thành công: " + textGridFile.getAbsolutePath());
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
        public FleCohAnswer analyzeProsodyFeatures(String audioUrl, JsonNode root) {
            try {
                File mp3File = downloadAudioFile(audioUrl);
                File wavFile = convertMp3ToWav(mp3File);
                File textGridFile = generateTextGridFromJson(root, wavFile);
                return runPraatAnalysis(wavFile, textGridFile);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        public PronunciationAnswer analyze(String audioUrl, JsonNode root) throws IOException, InterruptedException {
            System.out.println("\n=======================================");
            System.out.println("🚀 STARTING PROSODY ANALYSIS");
            System.out.println("   Audio URL: " + audioUrl);
            System.out.println("   JSON data: " + root.toString());
            System.out.println("=======================================\n");
            stressMismatches.clear();
            List<Map<String, Object>> stressMismatchesDetailed = new ArrayList<>();
            List<Map<String, Object>> pronunciationEvaluationList = new ArrayList<>();
            PronunciationAnswer result = new PronunciationAnswer();
            // 1. Tải và chuyển đổi file âm thanh
            File mp3File = downloadAudioFile(audioUrl);
            File wavFile = convertMp3ToWav(mp3File);

            // 2. Tạo TextGrid
            File textGridFile = generateTextGridFromJson(root, wavFile);

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
                // Bỏ qua từ không phải chữ cái (ví dụ số, ký tự đặc biệt)
                if (!detected.word.matches("^[a-zA-Z]+$")) continue;
                Integer standardPosition = getStandardStressPosition(detected.word);
                if (standardPosition == null) continue; // Bỏ qua nếu không có trong từ điển
                if (!standardPosition.equals(detected.detectedPosition)) {
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
            List<IntonationSentence> intonationResults = analyzeSentenceIntonation(wavFile, textGridFile);

            // 6. Tính điểm tổng hợp

            // 7. Tổng hợp kết quả
            // 8. Phân tích và chấm điểm Pronunciation bằng AI
            String transcript = null;
            if (root.has("text")) {
                transcript = root.get("text").asText();
                // Tách transcript thành từng từ để mapping index
                List<String> transcriptWords = new ArrayList<>();
                List<Integer> transcriptWordOffsets = new ArrayList<>();
                if (transcript != null) {
                    int offset = 0;
                    for (String word : transcript.split("(\\s+|(?=[,.!?;:]))")) {
                        if (!word.trim().isEmpty()) {
                            transcriptWords.add(word);
                            transcriptWordOffsets.add(offset);
                        }
                        offset++;
                    }
                }

                // 1. importantWords: từ AI trả về (evaluatePronunciation)
                List<IntonationSentence> importantWords = new ArrayList<>();
                List<IntonationSentence> aiImportant = evaluatePronunciation(transcript);
                if (aiImportant != null) {
                    for (IntonationSentence p : aiImportant) {
                        int idx = -1;
                        for (int i = 0; i < transcriptWords.size(); i++) {
                            if (transcriptWords.get(i).replaceAll("[^a-zA-Z]", "").equalsIgnoreCase(p.getText().replaceAll("[^a-zA-Z]", ""))) {
                                idx = i;
                                break;
                            }
                        }
                        importantWords.add(new IntonationSentence(p.getText(), idx));
                    }
                }

                // 2. emphasizedWords: từ thực tế nhấn mạnh (intonationResults)
                List<IntonationSentence> emphasizedWords = new ArrayList<>();
                for (IntonationSentence i : intonationResults) {
                    int idx = -1;
                    for (int j = 0; j < transcriptWords.size(); j++) {
                        if (transcriptWords.get(j).replaceAll("[^a-zA-Z]", "").equalsIgnoreCase(i.getText().replaceAll("[^a-zA-Z]", ""))) {
                            idx = j;
                            break;
                        }
                    }
                    emphasizedWords.add(new IntonationSentence(i.getText(), idx));
                }

                // 3. correctEmphasizedWords: giao giữa 2 list trên (theo text, không phân biệt hoa thường)
                List<IntonationSentence> correctEmphasizedWords = new ArrayList<>();
                for (IntonationSentence imp : importantWords) {
                    for (IntonationSentence emp : emphasizedWords) {
                        if (imp.getText().equalsIgnoreCase(emp.getText())) {
                            correctEmphasizedWords.add(new IntonationSentence(emp.getText(), emp.getIndex()));
                            break;
                        }
                    }
                }

                // 4. overEmphasis: từ bạn nhấn nhưng không cần nhấn (có trong emphasizedWords nhưng không có trong importantWords)
                List<IntonationSentence> overEmphasis = new ArrayList<>();
                for (IntonationSentence emp : emphasizedWords) {
                    boolean found = false;
                    for (IntonationSentence imp : importantWords) {
                        if (imp.getText().equalsIgnoreCase(emp.getText())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        overEmphasis.add(emp);
                    }
                }

                // 5. missingEmphasis: từ cần nhấn nhưng bạn không nhấn (có trong importantWords nhưng không có trong emphasizedWords)
                List<IntonationSentence> missingEmphasis = new ArrayList<>();
                for (IntonationSentence imp : importantWords) {
                    boolean found = false;
                    for (IntonationSentence emp : emphasizedWords) {
                        if (imp.getText().equalsIgnoreCase(emp.getText())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missingEmphasis.add(imp);
                    }
                }

                // === Gọi AI để chấm điểm pronunciation ===
                List<StressMismatch> stressList = result.getStressMismatchesDetailed();
                if (stressList == null) stressList = Collections.emptyList();
                double score = callOpenAIScorePronunciation(
                        transcript,
                        stressList,
                        intonationResults,
                        importantWords.stream().map(IntonationSentence::getText).collect(Collectors.toList()),
                        emphasizedWords.stream().map(IntonationSentence::getText).collect(Collectors.toList()),
                        correctEmphasizedWords.stream().map(IntonationSentence::getText).collect(Collectors.toList())
                );
                result.setScore(score);

                // Set các list vào PronunciationAnswer
                result.setImportantWords(importantWords);
                result.setEmphasizedWords(emphasizedWords);
                result.setCorrectEmphasizedWords(correctEmphasizedWords);
                result.setOverEmphasis(overEmphasis);
                result.setMissingEmphasis(missingEmphasis);
            }

            result.setStressMismatchesDetailed(stressMismatchesDetailed
                    .stream()
                    .map(map -> new StressMismatch(
                            (String) map.get("word"),
                            (Integer) map.get("detectedPosition"),
                            (Integer) map.get("standardPosition"),
                            (Double) map.get("start"),
                            (Double) map.get("end"),
                            (Integer) map.get("index")
                    )).collect(Collectors.toList())
            );

            result.setStressTranscript(stressTranscript(transcript));
            result.setTranscript(transcript);

            System.out.println("\n=======================================");
            System.out.println("🎉 ANALYSIS COMPLETED SUCCESSFULLY");
            System.out.println("   Final result: " + result);
            System.out.println("=======================================");

            return result;
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
            Map<String, Integer> wordSyllableCountMap = new HashMap<>();
            // Đọc lại TextGrid để lấy số âm tiết từng từ
            try (BufferedReader tgReader = new BufferedReader(new FileReader(textGridFile))) {
                String line;
                boolean inSyllableTier = false;
                int wordIdx = 0;
                while ((line = tgReader.readLine()) != null) {
                    if (line.trim().equals("name = \"syllables\"")) {
                        inSyllableTier = true;
                    }
                    if (inSyllableTier && line.trim().startsWith("text = ")) {
                        String text = line.trim().substring(7).replaceAll("[\"\']", "");
                        int syllableCount = 1;
                        try {
                            syllableCount = Integer.parseInt(text);
                        } catch (NumberFormatException ignored) {}
                        // Lưu theo thứ tự từ xuất hiện
                        if (wordIdx < detectedStressWords.size()) {
                            wordSyllableCountMap.put(detectedStressWords.get(wordIdx).word, syllableCount);
                        }
                        wordIdx++;
                    }
                    // Kết thúc tier syllables
                    if (inSyllableTier && line.trim().startsWith("item [")) {
                        break;
                    }
                }
            }
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
            // So sánh với CMU Dictionary, truyền map syllable count
            Map<String, Integer> detectedStressMap = new HashMap<>();
            for (DetectedStressWord dsw : detectedStressWords) {
                detectedStressMap.put(dsw.word, dsw.detectedPosition);
            }
            return resultBuilder.toString();
        }

        private List<IntonationSentence> analyzeSentenceIntonation(File wavFile, File textGridFile) {
            List<IntonationSentence> resultList = new ArrayList<>();
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

                // Parse kết quả đầu ra
                System.out.println("📊 [INTONATION] Parsing results from: " + outputFile.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                         if (line.contains("Emphasized word:")) {
                            String word = line.split("'")[1];
                            resultList.add(new IntonationSentence(word, -1));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("💥 Sentence intonation analysis error: " + e.getMessage());
                e.printStackTrace();
            }
            return resultList;
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


        private FleCohAnswer runPraatAnalysis(File wavFile, File textGridFile) throws IOException, InterruptedException {
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

            // Trả về FleCohAnswer từ parsePraatOutput
            return parsePraatOutput(outputFile);
        }



        private FleCohAnswer parsePraatOutput(File outputFile) throws IOException {
            FleCohAnswer answer = new FleCohAnswer();
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("=")) continue;
                    String[] parts = line.split("=");
                    if (parts.length != 2) continue;
                    String key = parts[0].trim();
                    String valueStr = parts[1].trim();
                    switch (key) {
                        case "meanIntensity":
                            answer.setMeanIntensity(valueStr);
                            break;
                        case "pauseCount":
                            answer.setPauseCount(valueStr);
                            break;
                        case "speechRate":
                            answer.setSpeechRate(valueStr);
                            break;
                    }
                }
            }
            // Set default values for score and comment
            answer.setScore(0);
            answer.setComment(null);
            System.out.println("📤 Parsed FleCohAnswer:\n" +
                    "meanIntensity=" + answer.getMeanIntensity() + ", pauseCount=" + answer.getPauseCount() + ", speechRate=" + answer.getSpeechRate());
            return answer;
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

        /**
         * Trả về transcript với các âm tiết mang trọng âm (theo CMU Dict) được viết hoa.
         * Ví dụ: "I believe that technological..." => "I beLIEVE that techNOlogical..."
         */
        public String stressTranscript(String transcript) {
            if (transcript == null || transcript.isEmpty()) return transcript;
            StringBuilder result = new StringBuilder();
            String[] words = transcript.split("(\\s+|(?=[,.!?;:]))"); // giữ dấu câu tách riêng
            for (String word : words) {
                if (word.trim().isEmpty()) {
                    result.append(word);
                    continue;
                }
                String cleanWord = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                String pronunciation = cmuDictMap.get(cleanWord);
                if (pronunciation == null || cleanWord.isEmpty()) {
                    result.append(word);
                } else {
                    // Tìm các âm tiết có số (vowel syllables)
                    String[] syllables = pronunciation.split("\\s+");
                    List<String> vowelSyllables = new ArrayList<>();
                    List<Integer> vowelIndexes = new ArrayList<>();
                    for (int i = 0, sylIdx = 0; i < syllables.length; i++) {
                        if (syllables[i].matches(".*[0-2]$")) {
                            vowelSyllables.add(syllables[i]);
                            vowelIndexes.add(i);
                            sylIdx++;
                        }
                    }
                    int stressSyllable = -1;
                    for (int i = 0; i < vowelSyllables.size(); i++) {
                        if (vowelSyllables.get(i).contains("1")) {
                            stressSyllable = i;
                            break;
                        }
                    }
                    // Nếu chỉ có 1 âm tiết nguyên âm thì KHÔNG viết hoa, giữ nguyên từ
                    if (vowelSyllables.size() == 1) {
                        result.append(word);
                    } else if (stressSyllable == -1) {
                        result.append(word);
                    } else {
                        // Tách từ thành các phần ứng với các âm tiết trong CMU
                        // Đơn giản hóa: chia đều các ký tự cho các âm tiết nguyên âm
                        // (không hoàn hảo nhưng đủ tốt cho hiển thị)
                        int len = word.replaceAll("[^a-zA-Z]", "").length();
                        int[] splitPoints = new int[vowelSyllables.size() + 1];
                        for (int i = 0; i <= vowelSyllables.size(); i++) {
                            splitPoints[i] = (int) Math.round((double) i * len / vowelSyllables.size());
                        }
                        String onlyLetters = word.replaceAll("[^a-zA-Z]", "");
                        StringBuilder stressedWord = new StringBuilder();
                        int letterIdx = 0;
                        for (int i = 0; i < vowelSyllables.size(); i++) {
                            String part = onlyLetters.substring(splitPoints[i], splitPoints[i+1]);
                            if (i == stressSyllable) {
                                stressedWord.append(part.toUpperCase());
                            } else {
                                stressedWord.append(part.toLowerCase());
                            }
                        }
                        // Thêm lại dấu câu nếu có
                        String nonLetter = word.replaceAll("[a-zA-Z]", "");
                        result.append(stressedWord);
                        result.append(nonLetter);
                    }
                }
                result.append(" ");
            }
            return result.toString().replaceAll("\\s+([,.!?;:])", "$1").trim();
        }
    }