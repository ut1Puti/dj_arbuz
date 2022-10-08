package httpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс http сервера
 *
 * @author Кедровских Олег
 * @version 0.2
 */
public class HttpServer {
    /**
     * Поле запущенного в программе сервера
     */
    private static HttpServer server = null;
    /**
     * Поле серверного сокета
     */
    private final ServerSocket serverSocket;

    /**
     * Конструктор - создание объекта
     *
     * @throws IOException - возникает при ошибке открытия серверного сокета
     */
    private HttpServer() throws IOException {
        serverSocket = new ServerSocket(HttpServerConfig.PORT);
    }

    /**
     * Метод возращающий объект сервер запущенный в программе
     *
     * @return возвращает объект сервера
     * @throws IOException - возникает при ошибке открытия серверного сокета
     */
    public static HttpServer getInstance() throws IOException {
        if (server == null) {
            server = new HttpServer();
        }
        return server;
    }

    /**
     * Метод обрабатывающий запрос и возвращающий get параметры запроса на сервер
     *
     * @return возвращает get параметры запроса на сервер
     * @throws IOException - возникает:
     *                     при открытии сокета,
     *                     открытии потока ввода или вывода
     */
    public String getHttpRequestGetParametrs() throws IOException {
        String requestParameters;

        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        HttpRequest request;
        try {
            request = HttpParser.parseRequestLine(inputStream);
        } catch (HttpParserException e) {
            return null;
        }

        StringBuilder fileName = new StringBuilder();
        boolean isFileNameGot = false;
        StringBuilder requestParametersBuilder = new StringBuilder();
        for (char ch : request.requestTarget.toCharArray()) {
            if (ch == '?') {
                isFileNameGot = true;
            }
            if (isFileNameGot) {
                requestParametersBuilder.append(ch);
            } else {
                fileName.append(ch);
            }
        }
        requestParameters = requestParametersBuilder.toString();

        String response = HttpResponse.createResponse(fileName.toString());
        if (response != null) {
            outputStream.write(response.getBytes());
        }

        inputStream.close();
        outputStream.close();
        socket.close();

        return requestParameters;
    }

    /**
     * Метод останавливающий сервер
     *
     * @throws IOException - возникает при ошибке закрытия сокета
     */
    public void stop() throws IOException {
        serverSocket.close();
        server = null;
    }
}
