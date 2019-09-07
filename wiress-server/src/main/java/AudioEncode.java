
import be.tarsos.dsp.AudioGenerator;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.synthesis.SineGenerator;
import be.tarsos.dsp.writer.WriterProcessor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioEncode {

    static void dataToWav(byte[] data) {
        try {

            double sampleRate = 44100.0;
            double amplitude = 1.0;
            int samplesPerNote = (int)sampleRate / 10;
            double seconds = data.length * samplesPerNote / sampleRate;

            float[] buffer = new float[(int) (seconds * sampleRate)];

            for (int i = 0; i < data.length; i++) {
                int freq = data[i] * 100 + 200;
                for (int sample = i * samplesPerNote; sample < (i + 1) * samplesPerNote - 1; sample++) {
                    buffer[sample] = (float) (Math.sin(2 * Math.PI * sample * freq / sampleRate));
                }
            }

            final byte[] byteBuffer = new byte[buffer.length * 2];
            int bufferIndex = 0;
            for (int i = 0; i < byteBuffer.length; i++) {
                final int x = (int) (buffer[bufferIndex++] * 32767.0);
                byteBuffer[i] = (byte) x;
                i++;
                byteBuffer[i] = (byte) (x >>> 8);
            }
            File out = new File("out.wav");
            boolean bigEndian = false;
            boolean signed = true;
            int bits = 16;
            int channels = 1;
            AudioFormat format;
            format = new AudioFormat((float) sampleRate, bits, channels, signed, bigEndian);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
            AudioInputStream audioInputStream;
            audioInputStream = new AudioInputStream(bais, format, buffer.length);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
            audioInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(AudioEncode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
