package httpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//сделать javadoc(весь пакет)
//переписать с вменяемыми переменными и методами(весь пакет)
public class HttpServer {
    private static HttpServer server = null;
    private ServerSocket serverSocket;

    private HttpServer() throws IOException {
        serverSocket = new ServerSocket(HttpServerConfig.PORT);
    }

    public static HttpServer getInstance() throws IOException {
        if (server == null){
            server = new HttpServer();
        }
        return server;
    }

    public String getCode() throws IOException{
        String code;

        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        String request = HttpParser.parseRequest(inputStream);
        //System.out.println(r);
        String[] arr = request.split("\n");
        String file = arr[0].split(" ")[1];

        StringBuilder sb = new StringBuilder();
        boolean g = false;
        StringBuilder codeBuilder = new StringBuilder();
        for (char ch : file.toCharArray()) {
            if (ch == '?') {
                g = true;
            }
            if (g) {
                codeBuilder.append(ch);
            } else {
                sb.append(ch);
            }
        }
        code = codeBuilder.toString();

        String response = HttpResponse.createResponse(sb.toString());
        if (response != null) {
            outputStream.write(response.getBytes());
        }

        inputStream.close();
        outputStream.close();
        socket.close();

        return code;
    }

    public void stop() throws IOException {
        serverSocket.close();
        server = null;
    }
}
