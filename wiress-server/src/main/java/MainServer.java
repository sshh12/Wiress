
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import static spark.Spark.*;
import spark.*;

public class MainServer {

    public static void main(String[] args) {
        port(8080);
        webSocket("/voice", WebSocketHandler.class);
        get("/resp", (request, responce) -> getFile(request, responce));
        init();
    }

    private static Object getFile(Request request, Response response) {
        System.out.println("/resp!!");
        response.header("Cache-Control", "no-cache");
        response.header("Content-Type", "audio/wav");
        response.header("Content-disposition", "attachment; filename=test.wav;");
        File file = new File("C:\\test.wav");
        try {
            OutputStream outputStream = response.raw().getOutputStream();
            outputStream.write(Files.readAllBytes(file.toPath()));
            outputStream.flush();
        } catch (IOException ex) {
        }
        return response;
    }
}
