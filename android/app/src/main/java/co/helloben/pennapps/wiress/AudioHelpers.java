package co.helloben.pennapps.wiress;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class AudioHelpers {
    private String inWav;
    private String outWav;
    Context context;

    public AudioHelpers(Context context) {
        this.context = context;
        filenames = new ArrayList<>();
    }

    public void GenerateWAV(String encode) {
        // Generate filename
        inWav = UUID.randomUUID().toString() + ".wav";

        // Actually generate WAV using library



    }

    public void PlayGeneratedWAV(final MainActivity.BensBadCallbackInterface cb) {
        // Play file through speakers at ultra max volume

        // todo: instad of being from RAW, this should be some internal URI.
        // https://developer.android.com/guide/topics/media/mediaplayer#java
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ahem_x);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // whalla
                cb.onCompletion(true);
            }
        });
        mediaPlayer.start();
    }



    public String DecodeRecordedWAV() {
        // Pass file to library
        return "<html><body><h1>Hello World!!</h1></body></html>";
    }

    private static final int BUFFER_SIZE_FACTOR = 2;

    private static final int SAMPLING_RATE_IN_HZ = 44100;

    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * Size of the buffer where the audio data is stored by Android
     */
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT) * BUFFER_SIZE_FACTOR;

    private final AtomicBoolean recordingInProgress = new AtomicBoolean(false);

    private AudioRecord recorder = null;

    private Thread recordingThread = null;

    private ArrayList<String> filenames;

    public void RecordWAVFromMic(final MainActivity.BensBadCallbackInterface cb) {
        // Generate filename

        Log.v("84", "here");
        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLING_RATE_IN_HZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        recordingInProgress.set(true);
        recorder.startRecording();

        recordingThread = new Thread(new RecordingRunnable(), "Recording Thread");
        recordingThread.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 5 seconds
                recorder.stop();
                recorder.release();
                recordingInProgress.set(false);
                cb.onCompletion(true);
            }
        }, 5000);

    }


    private class RecordingRunnable implements Runnable {

        @Override
        public void run() {
            final File file = new File(Environment.getExternalStorageDirectory(), "recording.pcm");
            final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            try (final FileOutputStream outStream = new FileOutputStream(file)) {
                while (recordingInProgress.get()) {
                    Log.v("118", "hello");

                    int result = recorder.read(buffer, BUFFER_SIZE);

                    if (result < 0) {
                        throw new RuntimeException("Reading of audio buffer failed: " + getBufferReadFailureReason(result));
                    }
//                    outStream.write(buffer.array(), 0, BUFFER_SIZE);

                    Log.i("buffer", bytesToHex(buffer.array()));
//                    Log.i("buffer", buffer.toString());



                    buffer.clear();
                }
            } catch (IOException e) {
                throw new RuntimeException("Writing of recorded audio failed", e);
            }
        }

        private String getBufferReadFailureReason(int errorCode) {
            switch (errorCode) {
                case AudioRecord.ERROR_INVALID_OPERATION:
                    return "ERROR_INVALID_OPERATION";
                case AudioRecord.ERROR_BAD_VALUE:
                    return "ERROR_BAD_VALUE";
                case AudioRecord.ERROR_DEAD_OBJECT:
                    return "ERROR_DEAD_OBJECT";
                case AudioRecord.ERROR:
                    return "ERROR";
                default:
                    return "Unknown (" + errorCode + ")";
            }
        }
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
