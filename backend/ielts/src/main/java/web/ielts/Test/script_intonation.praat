form Analyze Intonation by Intensity (30% cuối câu)
    sentence wav_file
    sentence textGridFile
    sentence output_file
endform

sound = Read from file: wav_file$
textGrid = Read from file: textGridFile$

selectObject: sound
intensity = To Intensity: 75, 0.0, "yes"

selectObject: textGrid
numSentences = Get number of intervals: 2
writeInfoLine: "Num sentences: ", numSentences

writeFileLine: output_file$, "SENTENCE|START|END|INTONATION_TYPE"

threshold = 50.0   ; cường độ tối thiểu tính là active speech (dB)
step = 0.01        ; bước lấy mẫu (s)

for i to numSentences
    label$ = Get label of interval: 2, i
    if label$ <> ""
        start = Get start time of interval: 2, i
        end = Get end time of interval: 2, i

        selectObject: intensity

        ; Tìm activeStart: thời điểm đầu tiên vượt ngưỡng
        activeStart = start
        found = 0
        t = start
        while t <= end and found = 0
            value = Get value at time: t, "linear"
            if value > threshold
                activeStart = t
                found = 1
            endif
            t = t + step
        endwhile

        ; Tìm activeEnd: thời điểm cuối cùng vượt ngưỡng
        activeEnd = end
        found = 0
        t = end
        while t >= start and found = 0
            value = Get value at time: t, "linear"
            if value > threshold
                activeEnd = t
                found = 1
            endif
            t = t - step
        endwhile

        ; Nếu đoạn thoại dài tối thiểu 50ms thì mới tính
        if activeEnd - activeStart > 0.05

            ; Tính khoảng tính slope: 30% cuối đoạn active speech
            slopeEnd = activeEnd
            slopeStart = activeEnd - 0.3 * (activeEnd - activeStart)
            if slopeStart < activeStart
                slopeStart = activeStart
            endif

            numSteps = floor ((slopeEnd - slopeStart) / step)
            n = numSteps + 1
            if n >= 2
                sumX = 0
                sumY = 0
                sumXY = 0
                sumXX = 0

                j = 0
                while j <= numSteps
                    t = slopeStart + j * step
                    value = Get value at time: t, "linear"
                    sumX = sumX + t
                    sumY = sumY + value
                    sumXY = sumXY + t * value
                    sumXX = sumXX + t * t
                    j = j + 1
                endwhile

                slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)

                ; Gán nhãn intonation theo độ dốc:
                if slope > 0.5
                    intonation$ = "falling"
                elsif slope < -0.5
                    intonation$ = "rising"
                else
                    intonation$ = "flat"
                endif

                appendFileLine: output_file$, "SENTENCE|", string$(i), "|", fixed$(start, 3), "|", fixed$(end, 3), "|", intonation$
            else
                writeInfoLine: "Too few points to compute slope in sentence ", i
            endif

        else
            writeInfoLine: "No valid active speech in sentence ", i
        endif
    endif
endfor

removeObject: sound, textGrid, intensity
