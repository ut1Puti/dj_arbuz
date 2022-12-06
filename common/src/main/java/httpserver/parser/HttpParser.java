package httpserver.parser;

import httpserver.messages.HttpStatusCode;
import httpserver.messages.request.HttpRequest;
import httpserver.server.HttpServerConfiguration;

import java.io.*;
import java.util.Scanner;

/**
 * Класс парсящий запрос поступивший на сервер
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpParser {
    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private HttpParser() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Метод парсящий запрос поступивший на сервер
     *
     * @param socketInputStream - поток данных с сокета
     * @return ?отпаршенный? запрос
     * @throws IOException - возникает при проблемах в InputStream с запросом
     * @throws HttpParserException - возникает при ошибках в полученном http запросе
     * @see HttpServerConfiguration#CRLF
     * @see HttpParser#parseRequestLine(Scanner, HttpRequest)
     * @see HttpParser#parseHeaders(Scanner, HttpRequest)
     * @see HttpParser#parseBody(Scanner, HttpRequest)
     */
    public static HttpRequest parseRequest(InputStream socketInputStream) throws IOException, HttpParserException {
        HttpRequest httpRequestParsed = new HttpRequest();
        Scanner httpRequestScanner = new Scanner(socketInputStream);
        httpRequestScanner.useDelimiter(HttpServerConfiguration.CRLF);
        parseRequestLine(httpRequestScanner, httpRequestParsed);
        parseHeaders(httpRequestScanner, httpRequestParsed);
        httpRequestScanner.reset();
        parseBody(httpRequestScanner, httpRequestParsed);
        return httpRequestParsed;
    }

    /**
     * Метод парсит request-line http запроса
     *
     * @param httpRequestScanner - сканер с потоком из которого читаются данные
     * @param httpRequest        - ?отпрашенный? запрос
     * @throws HttpParserException - возникает при ошибках в request-line http запроса
     * @see HttpRequest#setMethod(String)
     * @see HttpRequest#setRequestTarget(String)
     * @see HttpRequest#setHttpVersion(String)
     * @see HttpStatusCode#SERVER_ERROR_500_INTERNAL_SERVER_ERROR
     * @see HttpStatusCode#CLIENT_ERROR_400_BAD_REQUEST
     */
    private static void parseRequestLine(Scanner httpRequestScanner, HttpRequest httpRequest)
            throws HttpParserException {

        if (httpRequestScanner.hasNext()) {
            //TODO ignore if request starts with some CRLF
            String[] requestLine = httpRequestScanner.next().split(" ");

            if (requestLine.length != 3) {
                throw new HttpParserException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            }

            httpRequest.setMethod(requestLine[0]);
            httpRequest.setRequestTarget(requestLine[1]);
            httpRequest.setHttpVersion(requestLine[2]);
        } else {
            throw new HttpParserException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

    }

    /**
     * Метод парсящий headers http запроса
     *
     * @param httpRequestScanner - сканер с потоком из которого читаются данные
     * @param httpRequest        - ?отпрашенный? запрос
     * @see HttpRequest#headers
     * @see HttpServerConfiguration#CRLF
     */
    private static void parseHeaders(Scanner httpRequestScanner, HttpRequest httpRequest) {
        while (httpRequestScanner.hasNext()) {
            String read = httpRequestScanner.next();

            if (read.equals(HttpServerConfiguration.CRLF)) {
                break;
            }

            if (read.isBlank()) {
                break;
            }

            String[] header = read.split(": ", 2);
            httpRequest.headers.put(header[0], header[1]);
        }
    }

    /**
     * Метод парсящий body http запроса
     *
     * @param httpRequestScanner - сканер с потоком из которого читаются данные
     * @param httpRequest        - ?отпрашенный? запрос
     * @see HttpRequest#body
     */
    private static void parseBody(Scanner httpRequestScanner, HttpRequest httpRequest) {
        StringBuilder httpRequestBody = new StringBuilder();

        if (httpRequest.headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequest.headers.get("Content-Length"));
            int readLength = 0;
            while (httpRequestScanner.hasNext() && readLength <= contentLength) {
                String read = httpRequestScanner.next();
                httpRequestBody.append(read);
                readLength += read.length();
            }
        }

        httpRequest.body = httpRequestBody.toString();
    }
}
