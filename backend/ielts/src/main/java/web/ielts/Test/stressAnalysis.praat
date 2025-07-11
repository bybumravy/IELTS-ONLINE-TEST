form Analyze Word Stress with Syllables
    sentence wav_file
    sentence textGridFile
    sentence output_file
endform

# Read input files
sound = Read from file: wav_file$
textGrid = Read from file: textGridFile$

# Create intensity object
selectObject: sound
intensity = To Intensity: 100, 0.0, "yes"

# Tier numbers (adjust if needed)
selectObject: textGrid
wordTier = 1
syllableTier = 3

# Create output file header
writeFileLine: output_file$, "WORD_STRESS_ANALYSIS_WITH_SYLLABLES"
writeFileLine: output_file$, "Format: WORD_STRESS:word:syllableCount:stressedSyllable:maxIntensity:stressPosition:start:end"

# Analysis parameters
minIntensity = 50  ; minimum intensity (dB)
timeStep = 0.01    ; analysis step (s)

selectObject: textGrid
numWords = Get number of intervals: wordTier
numSyllables = Get number of intervals: syllableTier

for wordInterval to numWords
    selectObject: textGrid
    wordLabel$ = Get label of interval: wordTier, wordInterval

    if wordLabel$ <> "" and wordLabel$ <> "sp" and wordLabel$ <> "sil"
        wordStart = Get start time of interval: wordTier, wordInterval
        wordEnd = Get end time of interval: wordTier, wordInterval

        syllableCount = 0
        maxIntensity = -1000
        stressedSyllable = 1
        stressPosition = 0.5

        # Duyệt tất cả syllable intervals để tìm syllable thuộc về word này
        for s from 1 to numSyllables
            selectObject: textGrid
            syllableLabel$ = Get label of interval: syllableTier, s
            syllableStart = Get start time of interval: syllableTier, s
            syllableEnd = Get end time of interval: syllableTier, s

            if syllableStart >= wordStart and syllableEnd <= wordEnd and syllableLabel$ <> ""
                syllableCount = syllableCount + 1

                selectObject: intensity
                syllableIntensity = Get maximum: syllableStart, syllableEnd, "Parabolic"

                if syllableIntensity > maxIntensity
                    maxIntensity = syllableIntensity
                    stressedSyllable = syllableCount
                    stressPosition = (syllableStart + syllableEnd) / 2 - wordStart
                endif
            endif
        endfor


        if syllableCount > 0
            stressPosition = stressPosition / (wordEnd - wordStart)
            resultLine$ = "WORD_STRESS:" + wordLabel$ + ":" + string$(syllableCount) + ":" + string$(stressedSyllable) + ":" + string$(maxIntensity) + ":" + string$(stressPosition) + ":" + string$(wordStart) + ":" + string$(wordEnd)

            appendFileLine: output_file$, resultLine$
        endif

    endif
endfor

# Clean up
removeObject: sound, textGrid, intensity
