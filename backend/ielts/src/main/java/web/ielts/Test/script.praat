Text writing preferences: "UTF-8"

form AnalyzeProsody
    sentence soundFile
    sentence textGridFile
    sentence outputFile
endform

# Debug: thông báo bắt đầu (dùng writeInfo thay cho printline)
writeInfoLine: "Praat script start running"

# Load Sound
Read from file: soundFile$
sound = selected("Sound")
soundID = sound

# Load TextGrid
Read from file: textGridFile$
textgrid = selected("TextGrid")

# Tính Pitch
selectObject: soundID
To Pitch: 0, 75, 500
meanPitch = Get mean: 0, 0, "Hertz"
minPitch = Get minimum: 0, 0, "Hertz", "Parabolic"
maxPitch = Get maximum: 0, 0, "Hertz", "Parabolic"
Remove

# Tính Intensity
selectObject: soundID
To Intensity: 75, 0
meanIntensity = Get mean: 0, 0, "energy"
Remove

# Đếm pause trong TextGrid
selectObject: textgrid
pauseCount = Get number of intervals: 1

# Debug: in ra console (dùng writeInfo)
writeInfoLine: "meanPitch = ", meanPitch
writeInfoLine: "minPitch = ", minPitch
writeInfoLine: "maxPitch = ", maxPitch
writeInfoLine: "meanIntensity = ", meanIntensity
writeInfoLine: "pauseCount = ", pauseCount

# Ghi ra file (dùng writeFile và appendFile)
writeFile: outputFile$, "meanPitch=", string$(meanPitch), newline$
appendFile: outputFile$, "minPitch=", string$(minPitch), newline$
appendFile: outputFile$, "maxPitch=", string$(maxPitch), newline$
appendFile: outputFile$, "meanIntensity=", string$(meanIntensity), newline$
appendFile: outputFile$, "pauseCount=", string$(pauseCount), newline$

# Debug: thông báo kết thúc
writeInfoLine: "Have written: ", outputFile$
writeInfoLine: "Praat script end"