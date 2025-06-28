package web.ielts.Test.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.AudioEvent;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProsodyService {

    public Map<String, Object> analyze(String audioUrl, String transcript) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. Tải file mp3 từ S3
            File mp3File = downloadAudioFile(audioUrl);

            // 2. Chuyển mp3 → wav
            File wavFile = convertMp3ToWav(mp3File);

            // 3. Phân tích prosody bằng TarsosDSP
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(wavFile, 2048, 1024);
            List<Double> pitches = new ArrayList<>();
            List<Long> timestamps = new ArrayList<>();
            AtomicInteger count = new AtomicInteger(0);

            long[] lastTime = {0};

            dispatcher.addAudioProcessor(new PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                    44100, 2048,
                    (PitchDetectionResult pitchResult, AudioEvent event) -> {
                        float pitch = pitchResult.getPitch();
                        long time = (long) (event.getTimeStamp() * 1000); // ms

                        if (pitch > 50 && pitch < 500) {
                            pitches.add((double) pitch);
                            timestamps.add(time);
                            count.incrementAndGet();
                        }
                    }
            ));
            dispatcher.run();

            // 4. Tính toán thông số
            double avgPitch = pitches.stream().mapToDouble(d -> d).average().orElse(0);
            double maxPitch = pitches.stream().mapToDouble(d -> d).max().orElse(0);
            double minPitch = pitches.stream().mapToDouble(d -> d).min().orElse(0);
            double intonationRange = maxPitch - minPitch;

            double speakingDuration = count.get() * 1024.0 / 44100.0; // giây

            // 5. Đếm pause (khoảng cách > 300ms giữa 2 pitch)
            int pauseCount = 0;
            for (int i = 1; i < timestamps.size(); i++) {
                if (timestamps.get(i) - timestamps.get(i - 1) > 300) {
                    pauseCount++;
                }
            }

            // 6. Tính speech rate
            int wordCount = transcript.split("\\s+").length;
            double speechRate = wordCount / speakingDuration;

            // 7. Kết quả
            result.put("avgPitch", avgPitch);
            result.put("intonationRange", intonationRange);
            result.put("pauseCount", pauseCount);
            result.put("speechRate", speechRate);
            result.put("speakingDuration", speakingDuration);
            result.put("wordCount", wordCount);

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
