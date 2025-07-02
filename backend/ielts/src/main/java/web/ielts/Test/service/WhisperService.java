package web.ielts.Test.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
public class WhisperService {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    public String transcribe(String audioUrl) {
        try {
            File audioFile = downloadAudioFile(audioUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(openaiApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(audioFile));
            body.add("model", "whisper-1");
            body.add("language", "en");

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
    public JsonNode transcribeWithTimestamps(String audioUrl) throws IOException {
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
        return mapper.readTree(response.getBody());
    }
    private File downloadAudioFile(String url) throws IOException {
        File file = Files.createTempFile("audio-", ".mp3").toFile();
        try (InputStream in = new URL(url).openStream(); OutputStream out = new FileOutputStream(file)) {
            in.transferTo(out);
        }
        return file;
    }
}