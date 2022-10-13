package dj.arbuz.httpserver.server;

import dj.arbuz.httpserver.HttpRequest;
import dj.arbuz.httpserver.HttpResponse;
import dj.arbuz.httpserver.parser.HttpParser;
import dj.arbuz.httpserver.parser.HttpParserException;
import dj.arbuz.stoppable.StoppableThread;

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
    private final int fiveMinutesInMilliseconds = 30000;

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
        working = true;
        while (serverSocket.isBound() && working) {
            try {
                Socket socket = serverSocket.accept();
                if (socket.isBound() && socket.isConnected()) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {

                        if (socket.isBound() && socket.isConnected()) {
                            inputStream = socket.getInputStream();
                            outputStream = socket.getOutputStream();

                            HttpRequest request;
                            try {
                                request = HttpParser.parseRequestLine(inputStream);
                            } catch (HttpParserException e) {
                                throw new RuntimeException(e);
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
                            String requestParameters = requestParametersBuilder.toString();
                            boolean isOffered = false;

                            if (!requestParameters.isBlank()) {
                                isOffered = getParameter.offer(
                                        requestParameters, fiveMinutesInMilliseconds, TimeUnit.MILLISECONDS
                                );
                            }

                            sendFileFromServer(isOffered, fileName.toString(), outputStream);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException ignored) {}
                    HttpServerUtils.closeServerStream(outputStream);
                    HttpServerUtils.closeServerStream(inputStream);
                    HttpServerUtils.closeServerStream(socket);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        working = false;
    }

    /**
     * Отправляет файл с сервера
     *
     * @param isOffered    - был ли получен код авторизации от пользователя
     * @param filePath     - путь до запрошенного файла
     * @param outputStream - выходной поток(не тот что исполнения), куда будут записаны данные
     * @throws IOException - возникает при отсутствии файла или при ошибке записи в поток
     */
    private void sendFileFromServer(boolean isOffered, String filePath, OutputStream outputStream) throws IOException {
        String response;

        if (isOffered) {
            response = HttpResponse.createResponse(filePath);
        } else {
            response = HttpResponse.createResponse(HttpServerConfiguration.AUTH_TIME_EXPIRED_FILE);
        }

        if (response != null) {
            outputStream.write(response.getBytes());
        }

    }

    /**
     * Метод проверяющий работает ли поток
     *
     * @return true - если поток работает
     * false - если поток завершил работу
     */
    @Override
    public boolean isWorking() {
        return working && isAlive() && serverSocket.isBound();
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
     * null если в течение 5 минут параметры не пришли, или поток был прерван
     */
    public String getHttpRequestParameters() {
        try {
            return getParameter.poll(fiveMinutesInMilliseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
