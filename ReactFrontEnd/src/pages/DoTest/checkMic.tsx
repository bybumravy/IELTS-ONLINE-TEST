import { useState, useRef } from "react";
import {useNavigate, useParams} from "react-router-dom";

const VoiceRecorder = () => {
    const { testId } = useParams<{ testId: string }>();
    const [recording, setRecording] = useState(false);
    const [audioUrl, setAudioUrl] = useState<string | null>(null);
    const mediaRecorderRef = useRef<MediaRecorder | null>(null);
    const audioChunksRef = useRef<Blob[]>([]);
    const navigate = useNavigate();

    const startRecording = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            const mediaRecorder = new MediaRecorder(stream);
            mediaRecorderRef.current = mediaRecorder;
            audioChunksRef.current = [];

            mediaRecorder.ondataavailable = (event: BlobEvent) => {
                audioChunksRef.current.push(event.data);
            };

            mediaRecorder.onstop = () => {
                const audioBlob = new Blob(audioChunksRef.current, { type: "audio/webm" });
                const url = URL.createObjectURL(audioBlob);
                setAudioUrl(url);
            };

            mediaRecorder.onerror = (event: Event) => {
                const errorEvent = event as any;
                console.error("MediaRecorder error:", errorEvent.error);
            };

            mediaRecorder.start();
            setRecording(true);
        } catch (error) {
            console.error("Error starting recording:", error);
        }
    };

    const stopRecording = () => {
        if (mediaRecorderRef.current && recording) {
            mediaRecorderRef.current?.stop();
            setRecording(false);
        }
    };

    const handleStartTest = () => {
        navigate(`/checkMic/${testId}`);
    };

    return (
        <div>
            <button onClick={recording ? stopRecording : startRecording}>
                {recording ? "Stop Recording" : "Start Recording"}
            </button>

            <button onClick={handleStartTest} style={{ marginLeft: "10px" }}>
                Start Test
            </button>

            {audioUrl && (
                <div>
                    <h3>Preview:</h3>
                    <audio controls src={audioUrl}></audio>
                </div>
            )}
        </div>
    );
};

export default VoiceRecorder;