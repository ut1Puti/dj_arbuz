package httpserver.server;

import httpserver.messages.request.HttpRequest;
import httpserver.messages.response.HttpResponse;
import httpserver.parser.HttpParser;
import httpserver.parser.HttpParserException;
import stoppable.StoppableThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
    /**
     * Поле для синхронизации получения get параметров вне сервера
     */
    private final SynchronousQueue<String> getParameter = new SynchronousQueue<>();
    /**
     * Поле времени ожидания получения get параметров
     */
    private final int oneMinute = 1;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param serverSocket - серверный сокет, который слушает поток
     */
    public ServerListenerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Метод реализующий логику выполняемую внутри потока
     */
    @Override
    public void run() {
        while (serverSocket.isBound() && working && !isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                if (socket.isBound() && socket.isConnected()) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {

                        if (socket.isBound() && socket.isConnected()) {
                            inputStream = socket.getInputStream();
                            outputStream = socket.getOutputStream();

                            HttpRequest request = HttpParser.parseRequestLine(inputStream);

                            if (!request.getRequestTarget().getRequestTargetFile().isBlank()) {
                                boolean isOffered = getParameter.offer(
                                        request.getRequestTarget().getParameters(), oneMinute, TimeUnit.MINUTES
                                );

                                if (isOffered) {
                                    sendFileFromServer(request.getRequestTarget().getRequestTargetFile(), outputStream);
                                } else {
                                    sendFileFromServer("/timeexpired.html", outputStream);
                                }
                            } else {
                                sendFileFromServer("/timeexpired.html", outputStream);
                            }

                        }
                    } catch (IOException | HttpParserException e) {
                        continue;
                    } catch (InterruptedException ignored) {
                        break;
                    }
                    HttpServerUtils.closeServerStream(outputStream);
                    HttpServerUtils.closeServerStream(inputStream);
                    HttpServerUtils.closeServerStream(socket);
                }
            } catch (IOException e) {
                break;
            }
        }
        working = false;
    }

    /**
     * Отправляет файл с сервера
     *
     * @param filePath     - путь до запрошенного файла
     * @param outputStream - выходной поток(не тот что исполнения), куда будут записаны данные
     * @throws IOException - возникает при отсутствии файла или при ошибке записи в поток
     */
    private void sendFileFromServer(String filePath, OutputStream outputStream) throws IOException {
        String response = HttpResponse.createResponse(filePath);
        outputStream.write(response.getBytes());
    }

    /**
     * Метод проверяющий работает ли поток
     *
     * @return true - если поток работает
     * false - если поток завершил работу
     */
    @Override
    public boolean isWorking() {
        return isAlive() && (working && serverSocket.isBound());
    }

    /**
     * Метод останавливающий поток прерывая его
     */
    @Override
    public void stopWithInterrupt() {
        working = false;
        interrupt();
        HttpServerUtils.closeServerStream(serverSocket);
    }

    /**
     * Метод получающий get параметры последнего запроса
     *
     * @return полученные get параметры последнего запроса,
     * null если в течение 1 минуты параметры не пришли, или поток был прерван
     */
    public String getHttpRequestParameters() {
        try {
            return getParameter.poll(oneMinute, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
