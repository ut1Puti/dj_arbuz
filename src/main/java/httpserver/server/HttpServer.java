package httpserver.server;

import java.io.IOException;

public class HttpServer {
    private static HttpServer server = null;
    private ServerListenerThread thread;

    private HttpServer() throws IOException {
        thread = new ServerListenerThread();
    }

    public static HttpServer getInstance() throws IOException {
        if (server == null){
            server = new HttpServer();
        }
        return server;
    }

    public void start() throws IOException, InterruptedException {
        thread.start();
    }
}
