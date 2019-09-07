package co.helloben.pennapps.wiress;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String url;

    public static WebView thisWebView;
    EditText editText;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisWebView = findViewById(R.id.webview);
        editText = findViewById(R.id.plain_text_input);
//        editText.addTextChangedListener(textWatcher);

        statusText = findViewById(R.id.textView);


        final Button button = (Button) findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                url = editText.getText().toString();
                // Generate audio

                statusText.setText(R.string.generating);

                final AudioHelpers audioHelpers = new AudioHelpers(getApplicationContext());
                audioHelpers.GenerateWAV(url);

                if (checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("hello", "has audio perm");
                } else {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                }


                // Play audio max volume

                statusText.setText(R.string.playing);
                audioHelpers.PlayGeneratedWAV(new BensBadCallbackInterface() {
                    @Override
                    public void onCompletion(Object o) {
                        statusText.setText(R.string.listening);

                        // Start listening for audio from mic
                        audioHelpers.RecordWAVFromMic(new BensBadCallbackInterface() {
                            @Override
                            public void onCompletion(Object o) {
                                statusText.setText(R.string.decoding);

                                // Decode audio recording
                                String html = audioHelpers.DecodeRecordedWAV();
                                statusText.setText(R.string.displaying + url);
                                thisWebView.loadData(html, "text/html", null);
                            }
                        });
                    }
                });








            }
        });
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
        // todo: CANCEL THINGS
    }


    // todo: this will be called by audiohelpers to advance to the next step
    public interface BensBadCallbackInterface
    {
        void onCompletion(Object o);
    }


//
//    private final TextWatcher textWatcher = new TextWatcher() {
//        public void afterTextChanged(Editable s) {
//            url = editText.getText().toString();
//
//        }
//
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//    };
}
