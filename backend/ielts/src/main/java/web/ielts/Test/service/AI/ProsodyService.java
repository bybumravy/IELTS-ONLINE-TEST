package web.ielts.Test.service.AI;

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

        private final String PRAAT_PATH = "C:\\Users\\LAPTOP24H\\Downloads\\praat6438_win-intel64\\Praat.exe"; // Đường dẫn đến Praat trên server
        private final String PRAAT_SCRIPT_PATH = "D:\\Ki4\\PRJ\\SWP_SE1934_Group3\\backend\\ielts\\src\\main\\java\\web\\ielts\\Test\\script.praat"; // Script Praat
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
            throw new IOException("Praat exited with code " + exitCode);
        }

        if (durationLine == null || durationLine.trim().isEmpty()) {
            System.err.println("Praat output:\n" + output);
            throw new IOException("Không nhận được duration từ Praat output");
        }

        try {
            return Double.parseDouble(durationLine.trim());
        } catch (NumberFormatException e) {
            System.err.println("Praat output:\n" + output);
            throw new IOException("Không parse được duration: " + durationLine, e);
        }
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
                Map<String, Double> praatResults = runPraatAnalysis(wavFile, textGridFile);


                // 3. Xử lý kết quả từ Praat
                double avgPitch = praatResults.get("meanPitch");
                double intonationRange = praatResults.get("maxPitch") - praatResults.get("minPitch");
                int pauseCount = praatResults.get("pauseCount").intValue();

                // 4. Phân tích từng từ (sử dụng timestamps từ Praat nếu cần)
                List<String> emphasizedWords = analyzeWordEmphasis(root, praatResults);

                // 5. Tổng hợp kết quả
                result.put("avgPitch", avgPitch);
                result.put("intonationRange", intonationRange);
                result.put("pauseCount", pauseCount);
                result.put("emphasizedWords", emphasizedWords);

                return result;
            } catch (Exception e) {
                return Map.of("error", e.getMessage());
            }
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