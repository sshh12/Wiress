
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

@WebSocket
public class WebSocketHandler {

    private AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.ULAW,
            8000,
            8,
            1,
            160,
            50,
            true
    );
    private File ulawFile;
    private FileOutputStream uLawFOS;
    private String callSid;

    @OnWebSocketConnect
    public void connected(Session session) {
        System.out.println("CONNECTED");
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        try {
            JSONObject jo = new JSONObject(message);
            String event = jo.getString("event");

            if (event.equals("connected")) {
                System.out.println("CONNECTED" + message);
            }
            if (event.equals("start")) {
                System.out.println("STARTED" + message);

                if (ulawFile == null) {
                    callSid = jo.getJSONObject("start").getString("callSid");
                    ulawFile = new File(callSid + ".ulaw");
                    ulawFile.createNewFile();
                    uLawFOS = new FileOutputStream(ulawFile);
                }
            }
            if (event.equals("media")) {
                String payload = jo.getJSONObject("media").getString("payload");
                byte[] decodedBytes = Base64.getDecoder().decode(payload);
                uLawFOS.write(decodedBytes);

            }
        } catch (JSONException e) {
        } catch (IOException ex) {
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        try {
            if (uLawFOS != null)
                uLawFOS.close();
            File wavFile = new File(callSid + ".wav");
            FileOutputStream wavFOS = new FileOutputStream(wavFile);
            if (!wavFile.exists()) {
                wavFile.createNewFile();
            }
            AudioInputStream ais = new AudioInputStream(
                    new FileInputStream(ulawFile),
                    format,
                    ulawFile.length() / 160
            );
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFOS);
            wavFOS.close();

            // Hmm
            wavFOS = null;
            wavFile = null;
            ulawFile = null;
            uLawFOS = null;
        } catch (Exception ex) {
        }
    }
}
