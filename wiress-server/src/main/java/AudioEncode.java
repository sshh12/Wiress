
import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.WaveDecoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioEncode {

    final static double sampleRate = 44_100;

    static void dataToWav(byte[] data, String filename) {
        try {

            int samplesPerNote = (int) sampleRate / 8;
            double seconds = data.length * samplesPerNote / sampleRate;

            float[] buffer = new float[(int) (seconds * sampleRate)];

            for (int i = 0; i < data.length; i++) {
                byte b = data[i];
                float freq = byteToFreq(b);
                for (int sample = i * samplesPerNote; sample < (i + 1) * samplesPerNote; sample++) {
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
            AudioFormat format = new AudioFormat((float) sampleRate, 16, 1, signed, bigEndian);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
            AudioInputStream audioInputStream;
            audioInputStream = new AudioInputStream(bais, format, buffer.length);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
            audioInputStream.close();
        } catch (IOException ex) {
        }
    }

    static byte[] wavToBytes(String filename) {
        try {
            WaveDecoder decoder = new WaveDecoder(new FileInputStream(filename));

            float[] samples = new float[1024];

            FFT fft = new FFT(1024, (float) sampleRate);

            ArrayList<Byte> datas = new ArrayList<>();
            byte last = -1;

            while (decoder.readSamples(samples) > 0) {
                fft.forward(samples);
                byte b = dataFromSpec(fft);
                System.out.print(b);
            }
            System.out.println();

            byte[] bytes = new byte[datas.size()];
            for (int i = 0; i < datas.size(); i++) {
                bytes[i] = datas.get(i);
            }
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static byte dataFromSpec(FFT fft) {
        float[] spectrum = fft.getSpectrum();
        int best = -1;
        float bestScore = -1;
        int minIndex = fft.freqToIndex(byteToFreq((byte)0));
        int maxIndex = fft.freqToIndex(byteToFreq((byte)127));
        for (int i = minIndex; i <= maxIndex; i++) {
            float freq = fft.indexToFreq(i);
            float score = spectrum[i];
            if (score > bestScore) {
                bestScore = score;
                best = i;
            }
        }
        float freq = fft.indexToFreq(best);
        return freqToByte(freq);
    }

    static float byteToFreq(byte b) {
        return b * 100 + 400;
    }

    static byte freqToByte(float freq) {
        return (byte) ((freq - 400) / 100 + 0.5);
    }

}
