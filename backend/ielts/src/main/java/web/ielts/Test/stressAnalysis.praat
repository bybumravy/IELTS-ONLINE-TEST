    form Analyze Stress
        sentence wavFile
        sentence textGridFile
        sentence outputFile
    endform

    # Xóa file output cũ nếu tồn tại
    if fileReadable(outputFile$)
        deleteFile: outputFile$
    endif

    # Tạo file output mới
    writeFile: outputFile$, "ANALYSIS_START"

    # Đọc file input
    sound = Read from file: wavFile$
    textGrid = Read from file: textGridFile$

    selectObject: textGrid
    numberOfIntervals = Get number of intervals: 1

    for interval from 1 to numberOfIntervals
        selectObject: textGrid
        label$ = Get label of interval: 1, interval

        if label$ != ""
            start = Get start time of interval: 1, interval
            end = Get end time of interval: 1, interval

            selectObject: sound
            Extract part: start, end, "rectangular", 1, "no"
            soundPart = selected("Sound")

            To Intensity: 100, 0, "yes"
            intensity = selected("Intensity")

            maxIntensity = Get maximum: 0, 0, "Parabolic"
            maxIntensityTime = Get time of maximum: 0, 0, "Parabolic"

            # Ghi dữ liệu từng dòng
            line$ = "WORD_STRESS:" + label$ + ":" + string$(maxIntensity) + ":" + string$(maxIntensityTime) + ":" + string$(start) + ":" + string$(end)
            appendFileLine: outputFile$, line$

            removeObject: soundPart, intensity
        endif
    endfor

    appendFileLine: outputFile$, "ANALYSIS_END"