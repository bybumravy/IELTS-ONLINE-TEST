form AnalyzeProsody
    sentence soundFile
    sentence textGridFile
    sentence outputFile
endform

# Debug: thông báo bắt đầu
printline "Praat script bắt đầu chạy."

Read from file... 'soundFile$'
sound = selected("Sound")

Read from file... 'textGridFile$'
textgrid = selected("TextGrid")

duration = Get total duration

To Pitch... 75 500
meanPitch = Get mean: 0, 0, "Hertz"
minPitch = Get minimum: 0, 0, "Hertz", "Parabolic"
maxPitch = Get maximum: 0, 0, "Hertz", "Parabolic"
Remove

To Intensity... 75 0
meanIntensity = Get mean: 0, 0, "energy"
Remove

pauseCount = Get number of intervals: 1

# Debug: in ra console
printline "meanPitch = " + string$(meanPitch)
printline "minPitch  = " + string$(minPitch)
printline "maxPitch  = " + string$(maxPitch)
printline "meanIntensity = " + string$(meanIntensity)
printline "pauseCount = " + string$(pauseCount)

# Ghi ra file output
filedelete 'outputFile$'
fileappend 'outputFile$' "meanPitch=" + string$(meanPitch) + newline$
fileappend 'outputFile$' "minPitch=" + string$(minPitch) + newline$
fileappend 'outputFile$' "maxPitch=" + string$(maxPitch) + newline$
fileappend 'outputFile$' "meanIntensity=" + string$(meanIntensity) + newline$
fileappend 'outputFile$' "pauseCount=" + string$(pauseCount) + newline$

# Debug: thông báo xong
printline "Đã ghi file output vào: " + 'outputFile$'
printline "Praat script kết thúc."
