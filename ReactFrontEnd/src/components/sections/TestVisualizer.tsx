import { useRef, useEffect } from "react";

export default function TestVisualizer() {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const audioContextRef = useRef<AudioContext | null>(null);
    const analyserRef = useRef<AnalyserNode | null>(null);
    const animationRef = useRef<number | null>(null);

    useEffect(() => {
        async function start() {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            audioContextRef.current = new AudioContext();
            const source = audioContextRef.current.createMediaStreamSource(stream);
            analyserRef.current = audioContextRef.current.createAnalyser();
            source.connect(analyserRef.current);
            analyserRef.current.fftSize = 256;

            const bufferLength = analyserRef.current.frequencyBinCount;
            const dataArray = new Uint8Array(bufferLength);

            const canvas = canvasRef.current;
            const ctx = canvas?.getContext("2d");

            function draw() {
                if (!ctx || !analyserRef.current) return;

                analyserRef.current.getByteTimeDomainData(dataArray);

                ctx.fillStyle = "white";
                ctx.fillRect(0, 0, canvas!.width, canvas!.height);

                ctx.lineWidth = 2;
                ctx.strokeStyle = "red";

                ctx.beginPath();

                const sliceWidth = (canvas!.width * 1.0) / bufferLength;
                let x = 0;

                for (let i = 0; i < bufferLength; i++) {
                    const v = dataArray[i] / 128.0;
                    const y = (v * canvas!.height) / 2;

                    if (i === 0) {
                        ctx.moveTo(x, y);
                    } else {
                        ctx.lineTo(x, y);
                    }

                    x += sliceWidth;
                }

                ctx.lineTo(canvas!.width, canvas!.height / 2);
                ctx.stroke();

                animationRef.current = requestAnimationFrame(draw);
            }

            draw();
        }

        start();

        return () => {
            if (animationRef.current) cancelAnimationFrame(animationRef.current);
            if (audioContextRef.current) audioContextRef.current.close();
        };
    }, []);

    return <canvas ref={canvasRef} width={400} height={100} style={{ border: "1px solid #000" }} />;
}
