package web.ielts.Test.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.AudioEvent;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProsodyService {

    public Map<String, Object> analyze(String audioUrl, JsonNode root) {
        Map<String, Object> result = new HashMap<>();
        try {
            File mp3File = downloadAudioFile(audioUrl);
            File wavFile = convertMp3ToWav(mp3File);

            AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(wavFile, 2048, 1024);
            List<Double> pitches = new ArrayList<>();
            List<Long> timestamps = new ArrayList<>();
            AtomicInteger count = new AtomicInteger(0);

            dispatcher.addAudioProcessor(new PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                    44100, 2048,
                    (PitchDetectionResult pitchResult, AudioEvent event) -> {
                        float pitch = pitchResult.getPitch();
                        long time = (long) (event.getTimeStamp() * 1000);
                        if (pitch > 50 && pitch < 500) {
                            pitches.add((double) pitch);
                            timestamps.add(time);
                            count.incrementAndGet();
                        }
                    }
            ));
            dispatcher.run();

            double avgPitch = pitches.stream().mapToDouble(p -> p).average().orElse(0);
            double maxPitch = pitches.stream().mapToDouble(p -> p).max().orElse(0);
            double minPitch = pitches.stream().mapToDouble(p -> p).min().orElse(0);
            double intonationRange = maxPitch - minPitch;
            double speakingDuration = count.get() * 1024.0 / 44100.0;

            int pauseCount = 0;
            for (int i = 1; i < timestamps.size(); i++) {
                if (timestamps.get(i) - timestamps.get(i - 1) > 300) {
                    pauseCount++;
                }
            }

            // Lấy danh sách từ
            List<JsonNode> wordNodes = new ArrayList<>();
            if (root.has("segments")) {
                for (JsonNode segment : root.get("segments")) {
                    if (segment.has("words")) {
                        segment.get("words").forEach(wordNodes::add);
                    }
                }
            } else if (root.has("words")) {
                root.get("words").forEach(wordNodes::add);
            }

            int wordCount = wordNodes.size();
            double totalDuration = 0;
            List<String> emphasizedWords = new ArrayList<>();
            List<Map<String, Object>> words = new ArrayList<>();





            for (JsonNode word : wordNodes) {
                String w = word.get("word").asText();
                double start = word.get("start").asDouble();
                double end = word.get("end").asDouble();
                double duration = end - start;
                totalDuration += duration;

                List<Double> wordPitches = new ArrayList<>();
                for (int i = 0; i < timestamps.size(); i++) {
                    double timeSec = timestamps.get(i) / 1000.0;
                    if (timeSec >= start && timeSec <= end) {
                        wordPitches.add(pitches.get(i));
                    }
                }

                double maxP = wordPitches.stream().mapToDouble(p -> p).max().orElse(0);
                boolean isEmphasized = maxP > avgPitch * 1.5;

                Map<String, Object> wordMap = new HashMap<>();
                wordMap.put("word", w);
                wordMap.put("start", start);
                wordMap.put("end", end);
                wordMap.put("isEmphasized", isEmphasized);
                words.add(wordMap);

                if (isEmphasized) {
                    emphasizedWords.add(w);
                }
            }

            double speechRate = wordCount / speakingDuration;

            result.put("avgPitch", avgPitch);
            result.put("intonationRange", intonationRange);
            result.put("pauseCount", pauseCount);
            result.put("speechRate", speechRate);
            result.put("speakingDuration", speakingDuration);
            result.put("wordCount", wordCount);
            result.put("emphasizedWords", emphasizedWords);
            result.put("words", words);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Prosody analysis failed: " + e.getMessage());
        }
    }

    private File downloadAudioFile(String url) throws IOException {
        File file = Files.createTempFile("prosody-", ".mp3").toFile();
        try (InputStream in = new URL(url).openStream(); OutputStream out = new FileOutputStream(file)) {
            in.transferTo(out);
        }
        return file;
    }

    private File convertMp3ToWav(File mp3File) throws IOException, InterruptedException {
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
        return wavFile;
    }
}
