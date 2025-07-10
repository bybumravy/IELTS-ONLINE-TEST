# Sentence Intonation Analysis Script
# UTF-8 Encoding (No BOM)

form Analyze Sentence Intonation
    word Sound_file
    word TextGrid_file
    word Output_file
endform

# Read input files
sound = Read from file: sound_file$
textgrid = Read from file: textGrid_file$

# Get sentence tier number (change if needed)
sentenceTierNumber = 2

# Select TextGrid before getting intervals
selectObject: textgrid
numberOfSentences = Get number of intervals: sentenceTierNumber

# Initialize output file
if fileReadable(output_file$)
    deleteFile: output_file$
endif

# Create pitch object once for efficiency
selectObject: sound
pitch = To Pitch: 0, 75, 600

# Loop through each sentence interval
for sentence from 1 to numberOfSentences
    selectObject: textgrid
    sentenceLabel$ = Get label of interval: sentenceTierNumber, sentence

    if sentenceLabel$ <> ""
        sentenceStart = Get start time of interval: sentenceTierNumber, sentence
        sentenceEnd   = Get end time of interval: sentenceTierNumber, sentence

        # Get pitch values at start+0.1s and end-0.1s
        selectObject: pitch
        pitchStart = Get value at time: sentenceStart + 0.1, "Hertz", "linear"
        pitchEnd   = Get value at time: sentenceEnd - 0.1, "Hertz", "linear"

        if pitchStart != undefined and pitchEnd != undefined
            pitchDiff = pitchEnd - pitchStart

            if pitchDiff > 20
                type$ = "rising"
            elsif pitchDiff < -20
                type$ = "falling"
            else
                type$ = "flat"
            endif
        else
            type$ = "undefined"
        endif

        # Compose result line
line$ = "SENTENCE " + string$(sentence) + "|start=" + fixed$(sentenceStart, 3) + "|end=" + fixed$(sentenceEnd, 3) + "|pitchStart=" + fixed$(pitchStart, 1) + "|pitchEnd=" + fixed$(pitchEnd, 1) + "|type=" + type$

        # Append to output file
        appendFileLine: output_file$, line$
    endif
endfor

# Append analysis end mark
appendFileLine: output_file$, "ANALYSIS_END"

# Clean up
removeObject: pitch
removeObject: textgrid
removeObject: sound
