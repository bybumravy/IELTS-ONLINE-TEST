Text writing preferences: "UTF-8"

form AnalyzeProsody
    sentence soundFile
    sentence textGridFile
    sentence outputFile
endform

# Debug: thông báo bắt đầu
printline "Praat script start running"

# Load Sound
Read from file... 'soundFile$'
sound = selected("Sound")

# Giữ ID Sound để select lại sau
soundID = sound

# Load TextGrid
Read from file... 'textGridFile$'
textgrid = selected("TextGrid")

# Chuyển lại select Sound để xử lý
selectObject: soundID

# Lấy duration
duration = Get total duration

# Tính pitch (FIXED: added time step parameter as first argument)
To Pitch... 0 75 500
meanPitch = Get mean: 0, 0, "Hertz"
minPitch = Get minimum: 0, 0, "Hertz", "Parabolic"
maxPitch = Get maximum: 0, 0, "Hertz", "Parabolic"
Remove

# Chuyển lại select Sound để tính Intensity
selectObject: soundID
To Intensity... 75 0
meanIntensity = Get mean: 0, 0, "energy"
Remove

# Chuyển lại select TextGrid để đếm pause
selectObject: textgrid
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
printline "Have written: " + 'outputFile$'
printline "Praat script end"