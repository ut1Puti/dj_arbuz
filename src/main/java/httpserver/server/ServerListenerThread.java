package httpserver.server;

import httpserver.server.HttpServerConst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    private ServerSocket serverSocket;
    private String code = null;

    public ServerListenerThread() throws IOException {
        serverSocket = new ServerSocket(HttpServerConst.PORT);
    }

    @Override
    public void run(){
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                HttpConnectionWorkerThread thread = new HttpConnectionWorkerThread(socket);
                thread.start();
                while (code == null)
                    code = thread.getCode();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}
