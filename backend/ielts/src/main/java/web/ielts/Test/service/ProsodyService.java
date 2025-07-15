package web.ielts.Test.service;
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
import java.time.Instant;
import java.util.*;


@Service
public class ProsodyService {

    private final String PRAAT_PATH = "D:\\praat6438_win-intel64\\Praat.exe";
    private final String PRAAT_SCRIPT_PATH = "D:\\SWP_Project4\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script.praat"; // Script Praat
    private final String STRESS_ANALYSIS_SCRIPT_PATH = "D:\\SWP_Project4\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\stressAnalysis.praat";
    private final String INTONATION_SCRIPT_PATH = "D:\\SWP_Project4\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script_intonation.praat";
    @Value("${openai.api.key}")
    private String openaiApiKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> evaluatePronunciation(
            String transcript,
            String intonationAnalysis,
            String wordStressDetails
    ) {
        System.out.println("\n🔊 [PRONUNCIATION] Starting AI evaluation...");

        try {

            // 2. Xây dựng prompt chi tiết với dữ liệu đã parse
            String prompt = buildPronunciationPrompt(transcript, intonationAnalysis, wordStressDetails);

            // 3. Gọi API OpenAI
            String aiResponse = callOpenAIPronunciation(prompt);

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
            String intonationAnalysis,
            String wordStressDetails
    ) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a certified IELTS Speaking examiner. Your task is to evaluate the PRONUNCIATION skill ONLY based on both human and technical perspectives.\n\n");

        prompt.append("=== IELTS Pronunciation Band Descriptors (summarized) ===\n");
        prompt.append("- Band 9: Full control of pronunciation features (intonation, connected speech, stress). Speech is effortless to understand.\n");
        prompt.append("- Band 8: Wide range of pronunciation features with occasional lapses. L1 accent has minimal impact on intelligibility.\n");
        prompt.append("- Band 7: Good range of features, some lapses. Intelligibility is generally high despite some influence from L1 accent.\n");
        prompt.append("- Band 6: Uses pronunciation features with inconsistent control. Speech may have occasional mispronunciations affecting clarity.\n");
        prompt.append("- Band 5: Limited control of pronunciation features. Mispronunciations are frequent and may hinder understanding.\n\n");

        prompt.append("=== Technical Analysis ===\n");


        prompt.append("\n2. Intonation Patterns:\n");
        prompt.append(intonationAnalysis);

        prompt.append("\n3. Word Stress Details:\n");
        prompt.append(wordStressDetails);

        prompt.append("\n=== Candidate Transcript ===\n");
        prompt.append(transcript).append("\n\n");

        prompt.append("=== TASK ===\n");
        prompt.append("1. Evaluate the candidate’s **pronunciation** based on the IELTS Band Descriptors above and technical analysis provided.\n");
        prompt.append("2. Assign an IELTS Pronunciation score (in 0.5 band increments).\n");
        prompt.append("3. Identify 3–5 major pronunciation issues (e.g. misplaced stress, unclear intonation, weak linking, L1 interference).\n");
        prompt.append("4. Provide specific, actionable recommendations for improvement.\n\n");

        prompt.append("=== RESPONSE FORMAT (strict JSON only) ===\n");
        prompt.append("{\n");
        prompt.append("  \"score\": 6.5,\n");
        prompt.append("  \"strengths\": [\"Uses rising intonation appropriately in yes-no questions\", \"Generally clear word boundaries\"],\n");
        prompt.append("  \"weaknesses\": [\"Flat intonation in statements\", \"Stress placed on the wrong syllables in multisyllabic words\"],\n");
        prompt.append("  \"recommendations\": [\"Practice using sentence stress to highlight key information\", \"Use shadowing technique with native speakers to improve intonation\"]\n");
        prompt.append("}\n");
        System.out.println(prompt.toString());
        return prompt.toString();
    }


    private String callOpenAIPronunciation(String jsonPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4",
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are an IELTS pronunciation expert. Respond in STRICT JSON format ONLY."),
                            Map.of("role", "user", "content", jsonPrompt)
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
                return root.path("choices").get(0).path("message").path("content").asText();
            } else {
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
        String scriptPath = new File("D:\\SWP_Project4\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\GetDuration.praat").getAbsolutePath();
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
            String praatResults = runPraatAnalysis(wavFile, textGridFile);

            // 4. Phân tích trọng âm từ (CHI TIẾT VỊ TRÍ)
            System.out.println("Trong am");
            String stressResults = analyzeWordStress(wavFile, textGridFile);

            // 5. Phân tích ngữ điệu câu (MỚI)
            System.out.println("Ngu dieu cau");
            String intonationResults = analyzeSentenceIntonation(wavFile, textGridFile);

            // 6. Tính điểm tổng hợp

            // 7. Tổng hợp kết quả
            // Trong phương thức analyze()
            result.put("wordStressDetails", stressResults);
            result.put("prosodyFeatures", praatResults);
            result.put("intonationAnalysis", intonationResults); // Sửa tên key thành "intonationAnalysis"
            System.out.println("\n=======================================");
            System.out.println("🎉 ANALYSIS COMPLETED SUCCESSFULLY");
            System.out.println("   Final result: " + result);
            System.out.println("=======================================");

//                 8. Phân tích và chấm điểm Pronunciation bằng AI
            if (root.has("text")) {
                String transcript = root.get("text").asText();
                Map<String, Object> pronunciationEvaluation = evaluatePronunciation(
                        transcript,
                        intonationResults,
                        stressResults
                );
                result.put("pronunciationEvaluation", pronunciationEvaluation);
            }

            return result;
        } catch (Exception e) {
            System.err.println("❌ ANALYSIS ERROR: " + e.getMessage());
            e.printStackTrace();
            return Map.of("error", e.getMessage(), "stackTrace", Arrays.toString(e.getStackTrace()));
        }
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
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> wordStressList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    Map<String, Object> wordStress = new HashMap<>();
                    wordStress.put("word", parts[0].trim());
                    wordStress.put("stressedSyllable", Integer.parseInt(parts[1].trim()));

                    wordStressList.add(wordStress);
                }
            }
        }

        results.put("wordStressDetails", wordStressList);

        // Chuyển Map sang JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);

        // In JSON ra terminal
        System.out.println("📤 JSON Stress Analysis Result:\n" + jsonResult);

        return jsonResult;
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