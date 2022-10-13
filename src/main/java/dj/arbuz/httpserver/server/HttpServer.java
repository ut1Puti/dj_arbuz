package dj.arbuz.httpserver.server;

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
    private ServerSocket serverSocket;
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
        listenerThread = new ServerListenerThread(serverSocket);
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
     * Метод проверяющий работает ли сервер
     *
     * @return true - если поток обработки запросов работает
     * false - если поток обработки запросов не работает
     */
    public boolean isWorking() {
        return listenerThread.isWorking();
    }
    /**
     * Метод останавливающий сервер
     */
    public void stop() {
        listenerThread.stopWithInterrupt();
        HttpServerUtils.closeServerStream(serverSocket);
        serverSocket = null;
        server = null;
    }
}
