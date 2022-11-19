package httpserver.server;

import httpserver.messages.request.HttpRequest;
import httpserver.messages.response.HttpResponse;
import httpserver.parser.HttpParser;
import httpserver.parser.HttpParserException;
import stoppable.StoppableThread;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Класс слушающий новые сообщения поступающие сокету
 *
 * @author Кедровских Олег
 * @version 0.3
 */
public class ServerListenerThread extends StoppableThread {
    /**
     * Поле серверного сокета, который слушает текущий поток
     */
    private final ServerSocket serverSocket;
    private String next = "";
    /**
     * Поле времени ожидания получения get параметров
     */
    private final int oneMinute = 1;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param serverSocket - серверный сокет, который слушает поток
     */
    public ServerListenerThread(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /**
     * Метод реализующий логику выполняемую внутри потока
     *
     * @see HttpRequest
     * @see HttpParser#parseRequest(InputStream)
     * @see HttpRequest#getRequestTarget()
     * @see ServerListenerThread#oneMinute
     * @see ServerListenerThread#sendFileFromServer(String, OutputStream)
     * @see HttpServerUtils#closeServerStream(Closeable)
     */
    @Override
    public void run() {
        while (serverSocket.isBound() && working.get()) {
            try (Socket socket = serverSocket.accept()){
                if (socket.isBound() && socket.isConnected()) {
                    try (InputStream inputStream = socket.getInputStream();
                         OutputStream outputStream = socket.getOutputStream()) {

                        if (socket.isBound() && socket.isConnected()) {

                            HttpRequest request = HttpParser.parseRequest(inputStream);

                            if (!request.getRequestTarget().getRequestTargetFile().isBlank()) {
                                next = request.getRequestTarget().getParameters();
                                sendFileFromServer(request.getRequestTarget().getRequestTargetFile(), outputStream);
                            } else {
                                sendFileFromServer("/timeexpired.html", outputStream);
                            }

                        }
                    } catch (HttpParserException ignored) {
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
        working.set(false);
    }

    /**
     * Отправляет файл с сервера
     *
     * @param filePath     - путь до запрошенного файла
     * @param outputStream - выходной поток(не тот что исполнения), куда будут записаны данные
     * @throws IOException - возникает при отсутствии файла или при ошибке записи в поток
     * @see HttpResponse#createResponse(String)
     */
    private void sendFileFromServer(String filePath, OutputStream outputStream) throws IOException {
        String response = HttpResponse.createResponse(filePath);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    /**
     * Метод проверяющий работает ли поток
     *
     * @return true - если поток работает
     * false - если поток завершил работу
     * @see StoppableThread#isWorking()
     */
    @Override
    public boolean isWorking() {
        return super.isWorking() && serverSocket.isBound();
    }

    /**
     * Метод останавливающий поток прерывая его
     *
     * @see StoppableThread#stopWithInterrupt()
     * @see HttpServerUtils#closeServerStream(Closeable)
     */
    @Override
    public void stopWithInterrupt() {
        super.stopWithInterrupt();
        HttpServerUtils.closeServerStream(serverSocket);
    }

    /**
     * Метод получающий get параметры последнего запроса
     *
     * @return полученные get параметры последнего запроса,
     * null если в течение 1 минуты параметры не пришли, или поток был прерван
     */
    public String getHttpRequestParameters() {
        return next;
    }
}
