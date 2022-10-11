package httpserver.server;

import java.io.*;
import java.net.ServerSocket;

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
     * Поле серверных сокетов
     */
    private final ServerSocket serverSocket;
    /**
     * Поле потоков слушающих новые сообщения к сокету
     */
    private final ServerListenerThread listenerThread;


    /**
     * Конструктор - создание объекта
     *
     * @throws IOException - возникает при ошибке открытия серверного сокета
     */
    private HttpServer() throws IOException {
        serverSocket = new ServerSocket(HttpServerConfiguration.PORT);
        listenerThread = new ServerListenerThread(
                serverSocket, HttpServerConfiguration.AMOUNT_OF_WORKING_THREADS
        );
    }

    /**
     * Метод возвращающий объект сервера запущенного в программе
     *
     * @return возвращает объект сервера, если до этого сервера не было, то создает его
     * null если экземпляр сервера не был создан
     */
    public static HttpServer getInstance() {
        if (server == null) {
            try {
                server = new HttpServer();
            } catch (IOException e) {
                server = null;
            }
        }
        return server;
    }

    /**
     * Метод запускающий сервер
     */
    public void start() {
        listenerThread.start();
    }

    /**
     * Метод получающий get параметры последнего запроса
     *
     * @return полученные get параметры последнего запроса,
     * null если в течение 5 минут параметры не пришли, или поток был прерван
     */
    public String getHttpRequestParameters() {
        return listenerThread.getHttpRequestParameters();
    }

    /**
     * Метод останавливающий сервер
     */
    public void stop() {
        listenerThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server = null;
    }
}
