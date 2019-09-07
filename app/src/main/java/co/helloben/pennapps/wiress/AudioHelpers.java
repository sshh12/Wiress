package co.helloben.pennapps.wiress;

import android.content.Context;
import android.media.MediaPlayer;
import java.util.UUID;


public class AudioHelpers {
    String inWav;
    String outWav;
    Context context;

    public AudioHelpers(Context context) {
        this.context = context;
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
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.out);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // whalla
                cb.onCompletion(true);
            }
        });
        mediaPlayer.start();
    }


    public void RecordWAVFromMic() {
        // Generate filename


        // Listen for and calibrate to initial tone.


        // After initial tone, start recording for specified length


        // Save to filename
    }

    public String DecodeRecordedWAV() {
        // Pass file to library
        return "";
    }


}
