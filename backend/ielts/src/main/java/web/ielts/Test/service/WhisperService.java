package web.ielts.Test.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WhisperService {
    @Value("${openai.api.key}")
    private String openaiApiKey;
    @Value("${cmudict.file.path}")
    private String cmuDictPath;
    @PostConstruct
    public void init() throws IOException {
        File dictFile = new File(cmuDictPath); // ✅ Tự động dùng đường dẫn từ config
        CMUDictionary.loadDict(dictFile);
    }
    public String transcribe(String audioUrl) {
        try {
            File audioFile = downloadAudioFile(audioUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(openaiApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(audioFile));
            body.add("model", "whisper-1");
            body.add("response_format", "verbose_json");

            // ❌ Không thêm body.add("language", ...) để Whisper tự phát hiện

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/audio/transcriptions",
                    request,
                    String.class);

            JsonNode json = new ObjectMapper().readTree(response.getBody());
            return json.has("text") ? json.get("text").asText() : "";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during transcription: " + e.getMessage();
        }
    }



    public JsonNode transcribeWithTimestampsAndSyllables(String audioUrl) throws IOException {
        File audioFile = downloadAudioFile(audioUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(openaiApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", "whisper-1");
        body.add("language", "en");
        body.add("response_format", "verbose_json");
        body.add("timestamp_granularities[]", "word");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/audio/transcriptions",
                request,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());

        // Thêm thông tin số âm tiết vào kết quả
        if (rootNode.has("words")) {
            ArrayNode wordsNode = (ArrayNode) rootNode.get("words");
            for (JsonNode wordNode : wordsNode) {
                if (wordNode.has("word")) {
                    String word = wordNode.get("word").asText();
                    int syllableCount = CMUDictionary.countSyllables(word);
                    ((ObjectNode) wordNode).put("syllables", syllableCount);
                }
            }
        }

        return rootNode;
    }

    // Phương thức đếm số âm tiết (đơn giản)
//    private int countSyllables(String word) {
//        if (word == null || word.isEmpty()) {
//            return 0;
//        }
//
//        word = word.toLowerCase().replaceAll("[^a-z]", "");
//
//        if (word.length() == 0) {
//            return 0;
//        }
//
//        // Quy tắc đếm âm tiết đơn giản
//        int count = 0;
//        boolean prevVowel = false;
//
//        for (int i = 0; i < word.length(); i++) {
//            char c = word.charAt(i);
//            boolean isVowel = (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y');
//
//            if (isVowel && !prevVowel) {
//                count++;
//            }
//            prevVowel = isVowel;
//        }
//
//        // Điều chỉnh một số trường hợp đặc biệt
//        if (word.endsWith("e") && count > 1) {
//            count--;
//        }
//        if (word.endsWith("le") && count == 1) {
//            count++;
//        }
//
//        return Math.max(1, count); // Ít nhất 1 âm tiết
//    }

    private File downloadAudioFile(String url) throws IOException {


        // ✅ Kiểm tra và báo lỗi nếu URL không hợp lệ
        if (url == null || url.trim().isEmpty() || !url.startsWith("http")) {
            throw new IllegalArgumentException("❌ Invalid or empty URL: " + url);
        }

        File file = File.createTempFile("audio-", ".mp3");
        file.deleteOnExit();

        try (InputStream in = new URL(url).openStream();
             OutputStream out = new FileOutputStream(file)) {
            in.transferTo(out);
            System.out.println("✅ Audio file downloaded to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Error downloading file from URL: " + url);
            e.printStackTrace();
            throw e; // vẫn ném ra để xử lý bên trên nếu cần
        }

        return file;
    }
}