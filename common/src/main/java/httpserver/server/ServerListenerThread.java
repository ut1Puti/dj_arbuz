package httpserver.server;

import httpserver.messages.request.HttpRequest;
import httpserver.messages.response.HttpResponse;
import httpserver.parser.HttpParser;
import httpserver.parser.HttpParserException;
import stoppable.StoppableThread;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс слушающий новые сообщения поступающие сокету
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ServerListenerThread extends StoppableThread {
    /**
     * Поле серверного сокета, который слушает текущий поток
     */
    private final ServerSocket serverSocket;
    /**
     * Поле потока в который записываются полученные сервером get параметры http запроса
     */
    private final PipedOutputStream parametersOutputStream = new PipedOutputStream();
    /**
     * Поле reader'а читающего параметры полученных сервером get параметров http запросов
     */
    private final BufferedReader parametersReader;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param serverSocket - серверный сокет, который слушает поток
     */
    public ServerListenerThread(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        parametersReader = new BufferedReader(new InputStreamReader(new PipedInputStream(parametersOutputStream)));
    }


    /**
     * Метод реализующий логику выполняемую внутри потока
     *
     * @see HttpRequest
     * @see HttpParser#parseRequest(InputStream)
     * @see HttpRequest#getRequestTarget()
     * @see HttpServerUtils#closeServerStream(Closeable)
     */
    @Override
    public void run() {
        while (serverSocket.isBound() && working.get()) {
            try (Socket socket = serverSocket.accept()) {
                if (socket.isBound() && socket.isConnected()) {
                    try (InputStream inputStream = socket.getInputStream();
                         OutputStream outputStream = socket.getOutputStream()) {
                        try {

                            if (socket.isBound() && socket.isConnected()) {
                                HttpRequest request = HttpParser.parseRequest(inputStream);
                                HttpResponse response = HttpResponse.createResponseFromRequest(request);

                                outputStream.write(response.toHttpMessage().getBytes());
                                outputStream.flush();
                                parametersOutputStream.write((request.getRequestTarget().getParameters() + '\n').getBytes());
                                parametersOutputStream.flush();
                            }

                        } catch (HttpParserException e) {
                            HttpResponse response = HttpResponse.createFromHttpErrorCode(e.getErrorCode());
                            outputStream.write(response.toHttpMessage().getBytes());
                            outputStream.flush();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        working.set(false);
    }

    /**
     * Метод получающий get параметры последнего запроса
     *
     * @return полученные get параметры последнего запроса,
     * null если в течение 1 минуты параметры не пришли, или поток был прерван
     */
    public String getHttpRequestParameters() {
        try {
            return parametersReader.readLine();
        } catch (IOException e) {
            return "";
        }
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
        HttpServerUtils.closeServerStream(parametersOutputStream);
        HttpServerUtils.closeServerStream(parametersReader);
    }
}
