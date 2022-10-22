package httpserver.server;

import java.io.*;
import java.net.ServerSocket;

/**
 * Класс http сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpServer {
    /**
     * Поле запущенного в программе сервера
     *
     * @see HttpServer
     */
    private static HttpServer server;
    /**
     * Поле серверных сокетов
     */
    private ServerSocket serverSocket;
    /**
     * Поле потоков слушающих новые сообщения к сокету
     *
     * @see ServerListenerThread
     */
    private final ServerListenerThread listenerThread;


    /**
     * Конструктор - создание объекта
     *
     * @throws IOException - возникает при ошибке открытия серверного сокета
     * @see HttpServerConfiguration#PORT
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
                server.stop();
            }
        }

        return server;
    }

    /**
     * Метод запускающий сервер
     *
     * @see ServerListenerThread#start()
     */
    public void start() {
        listenerThread.start();
    }

    /**
     * Метод получающий get параметры последнего запроса
     *
     * @return полученные get параметры последнего запроса,
     * null если в течение 5 минут параметры не пришли, или поток был прерван
     * @see ServerListenerThread#getHttpRequestParameters()
     */
    public String getHttpRequestParameters() {
        return listenerThread.getHttpRequestParameters();
    }

    /**
     * Метод проверяющий работает ли сервер
     *
     * @return true - если поток обработки запросов работает,
     * false - если поток обработки запросов не работает
     * @see ServerListenerThread#isWorking()
     */
    public boolean isWorking() {
        return listenerThread.isWorking();
    }

    /**
     * Метод останавливающий сервер
     *
     * @see ServerListenerThread#stopWithInterrupt()
     * @see HttpServerUtils#closeServerStream(Closeable)
     */
    public void stop() {
        listenerThread.stopWithInterrupt();
        HttpServerUtils.closeServerStream(serverSocket);
        serverSocket = null;
        server = null;
    }
}
