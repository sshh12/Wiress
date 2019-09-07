
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import static spark.Spark.*;
import spark.*;

public class MainServer {

    final static String xmlTemplate = "<Response>\n"
            + "  <Start>\n"
            + "      <Stream name=\"stream_1_USER_ID\" url=\"wss://216ed9e9.ngrok.io/voice\">\n"
            + "          <Parameter name=\"id\" value =\"USER_ID\"/>\n"
            + "      </Stream>\n"
            + "  </Start>\n"
            + "  <Play>https://216ed9e9.ngrok.io/resp/USER_ID</Play>\n"
            + "  <Pause length=\"10\"/>\n"
            + "  <Stop>\n"
            + "   <Stream name=\"stream_1_USER_ID\" />\n"
            + "  </Stop>"
            + "</Response>";

    public static void main(String[] args) {
        // runServer();
        byte[] data = "UU<html class='v2' dir='ltr' lang='en-GB' xmlns='http://www.w3.org/1999/xhtml' xmlns:b='http://www.google.com/2005/gml/b' xmlns:data='http://www.google.com/2005/gml/data' ".getBytes();
        AudioEncode.dataToWav(data, "out.wav");
        byte[] dat = AudioEncode.wavToBytes("out.wav");
        System.out.println(new String(dat));
    }

    public static void runServer() {
        port(8080);
        webSocket("/voice", WebSocketHandler.class);
        get("/resp/:id", (request, responce) -> getFile(request, responce));
        post("/xml", (request, responce) -> getXML(request, responce));
        init();
    }

    private static Object getFile(Request request, Response response) {
        String id = request.params("id");
        System.out.println("/resp" + id);
        response.header("Cache-Control", "no-cache");
        response.header("Content-Type", "audio/wav");
        response.header("Content-disposition", "attachment; filename=test.wav;");
        AudioEncode.dataToWav(id.getBytes(), id + ".wav");
        File file = new File(id + ".wav");
        try {
            OutputStream outputStream = response.raw().getOutputStream();
            outputStream.write(Files.readAllBytes(file.toPath()));
            outputStream.flush();
        } catch (IOException ex) {
        }
        return response;
    }

    private static String getXML(Request request, Response response) {
        String body = request.body();
        String[] params = body.split("&");
        String caller = "???";
        String xml = xmlTemplate;
        for (String param : params) {
            if (param.contains("From=")) {
                caller = param.split("=")[1].replace("%2B", "+");
                break;
            }
        }
        xml = xml.replaceAll("USER_ID", caller);
        response.type("text/xml");
        System.out.println(caller);
        return xml;
    }

}
