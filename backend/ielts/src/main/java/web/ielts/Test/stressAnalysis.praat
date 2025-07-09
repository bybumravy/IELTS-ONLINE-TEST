Text writing preferences: "UTF-8"

form AnalyzeWordStress
    sentence soundFile
    sentence textGridFile
    sentence outputFile
endform

# Hiển thị thông báo bắt đầu
writeInfoLine: "Bắt đầu phân tích trọng âm từ..."
writeInfoLine: "Âm thanh: ", soundFile$
writeInfoLine: "TextGrid: ", textGridFile$
writeInfoLine: "--------------------------------------"

# Load Sound and TextGrid
Read from file: soundFile$
soundID = selected("Sound")
Read from file: textGridFile$
textGridID = selected("TextGrid")

# Get number of words
selectObject: textGridID
numWords = Get number of intervals: 1

# Mở file output (xóa nội dung cũ nếu có)
writeFile: outputFile$, "Word Stress Analysis Results", newline$, newline$
appendInfoLine: "Tổng số từ cần phân tích: ", numWords

# Phân tích từng từ
for word from 1 to numWords
    selectObject: textGridID
    wordLabel$ = Get label of interval: 1, word

    if wordLabel$ != ""
        wordStart = Get start point: 1, word
        wordEnd = Get end point: 1, word

        # Hiển thị tiến trình
        appendInfoLine: "Đang xử lý từ ", word, "/", numWords, ": '", wordLabel$, "'"

        # Trích xuất đoạn âm thanh
        selectObject: soundID
        Extract part: wordStart, wordEnd, "rectangular", 1, "no"
        wordSound = selected("Sound")

        # Phân tích intensity
        To Intensity: 100, 0, "yes"
        intensity = selected("Intensity")
        meanIntensity = Get mean: wordStart, wordEnd, "energy"

        # Phân tích pitch
        selectObject: wordSound
        To Pitch: 0, 75, 500
        pitch = selected("Pitch")
        meanPitch = Get mean: 0, 0, "Hertz"

        # Tính toán trọng âm
        normalizedIntensity = meanIntensity / 60
        normalizedPitch = meanPitch / 150
        stressLevel = (normalizedIntensity + normalizedPitch) / 2

        # Ghi kết quả vào file
        appendFileLine: outputFile$, "WORD_STRESS: ", wordLabel$, ": ", fixed$(stressLevel, 3)

        # Hiển thị kết quả ra terminal
        appendInfoLine: "  - Từ: ", wordLabel$
        appendInfoLine: "  - Intensity: ", fixed$(meanIntensity, 2)
        appendInfoLine: "  - Pitch: ", fixed$(meanPitch, 2)
        appendInfoLine: "  - Trọng âm: ", fixed$(stressLevel, 3)
        appendInfoLine: "--------------------------------------"

        # Dọn dẹp
        selectObject: intensity
        plusObject: pitch
        plusObject: wordSound
        Remove
    endif
endfor

# Dọn dẹp cuối cùng
selectObject: soundID
plusObject: textGridID
Remove

# Thông báo hoàn thành
appendFileLine: outputFile$, newline$, "Analysis completed at ", date$()
appendInfoLine: "Phân tích hoàn thành!"
appendInfoLine: "Kết quả đã được lưu vào: ", outputFile$