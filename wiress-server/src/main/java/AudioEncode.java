
import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.WaveDecoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioEncode {

    static void dataToWav(byte[] data, String filename) {
        try {

            double sampleRate = 44100.0;
            double amplitude = 1.0;
            int samplesPerNote = (int) sampleRate / 4;
            double seconds = data.length * samplesPerNote / sampleRate;

            float[] buffer = new float[(int) (seconds * sampleRate)];

            for (int i = 0; i < data.length; i++) {
                int freq = data[i] * 50 + 200;
                for (int sample = i * samplesPerNote; sample < (i + 1) * samplesPerNote - 1; sample++) {
                    buffer[sample] = (float) (Math.sin(2 * Math.PI * sample * freq / sampleRate));
                }
            }

            final byte[] byteBuffer = new byte[buffer.length * 2];
            int bufferIndex = 0;
            for (int i = 0; i < byteBuffer.length; i++) {
                final int x = (int) (buffer[bufferIndex] * 32767.0);
                bufferIndex++;
                byteBuffer[i] = (byte) x;
                byteBuffer[++i] = (byte) (x >>> 8);
            }
            File out = new File(filename);
            boolean bigEndian = false;
            boolean signed = true;
            AudioFormat format;
            format = new AudioFormat((float) sampleRate, 16, 1, signed, bigEndian);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
            AudioInputStream audioInputStream;
            audioInputStream = new AudioInputStream(bais, format, buffer.length);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
            audioInputStream.close();
        } catch (IOException ex) {
        }
    }

    static void wavToBytes(String filename) {
        try {
            WaveDecoder decoder = new WaveDecoder( new FileInputStream(filename) );

            float[] samples = new float[1024];

            FFT fft = new FFT(1024, 44100);
            float[] spectrum;

            while (decoder.readSamples(samples) > 0) {
                fft.forward(samples);
                spectrum = fft.getSpectrum();
                dataFromSpec(spectrum, fft);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void dataFromSpec(float[] spectrum, FFT fft) {
        int best = -1;
        float bestScore = -1;
        for(int i = 0; i < spectrum.length; i++) {
            if(spectrum[i] > bestScore) {
                bestScore = spectrum[i];
                best = i;
            }
        }
        float freq = fft.indexToFreq(best);
        byte data = (byte)((freq - 200) / 50 + 0.5);
        String test = new String(new byte[]{data});
        System.out.println(best + " " + (int)((freq - 200) / 50 + 0.5) + "  " + test);
    }

}
