package httpserver.parser;

import httpserver.messages.HttpStatusCode;
import httpserver.messages.request.HttpRequest;
import httpserver.server.HttpServerConfiguration;

import java.io.*;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Класс парсящий запрос поступивший на сервер
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpParser {
    /**
     * Поле пробела из таблицы ASCII
     */
    private static final int SP = 32;
    /**
     * Поле \r из таблицы ASCII
     */
    private static final int CR = 13;
    /**
     * Поле \n из таблицы ASCII
     */
    private static final int LF = 10;

    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private HttpParser() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Метод парсящий запрос поступивший на сервер
     *
     * @param socketInputStream поток данных с сокета
     * @return ?отпаршенный? запрос
     * @throws IOException         возникает при проблемах в InputStream с запросом
     * @throws HttpParserException возникает при ошибках в полученном http запросе
     */
    public static HttpRequest parseRequest(InputStream socketInputStream) throws IOException, HttpParserException {
        HttpRequest httpRequestParsed = new HttpRequest();
        InputStreamReader reader = new InputStreamReader(socketInputStream);
        parseRequestLine(reader, httpRequestParsed);
        parseHeaders(reader, httpRequestParsed);
        parseBody(reader, httpRequestParsed);
        return httpRequestParsed;
    }

    /**
     * Метод парсит request-line http запроса
     *
     * @param reader      поток из которого читаются данные
     * @param httpRequest ?отпрашенный? запрос
     * @throws HttpParserException возникает при ошибках в request-line http запроса
     * @see HttpRequest#setMethod(String)
     * @see HttpRequest#setRequestTarget(String)
     * @see HttpRequest#setHttpVersion(String)
     * @see HttpStatusCode#INTERNAL_SERVER_ERROR_500
     * @see HttpStatusCode#BAD_REQUEST_400
     */
    private static void parseRequestLine(InputStreamReader reader, HttpRequest httpRequest)
            throws HttpParserException, IOException {
        if (!reader.ready()) {
            throw new HttpParserException(HttpStatusCode.INTERNAL_SERVER_ERROR_500);
        }

        boolean isNoReadSymbols = true;
        boolean isNextMethod = true;
        boolean isNextRequestTarget = false;
        int _byte;
        StringBuilder nextPartOfRequest = new StringBuilder();
        while ((_byte = reader.read()) >= 0) {

            if (_byte == CR) {

                if ((_byte = reader.read()) == LF) {

                    if (isNoReadSymbols) {
                        continue;
                    }

                    if (isNextMethod || isNextRequestTarget) {
                        throw new HttpParserException(HttpStatusCode.BAD_REQUEST_400);
                    }

                    httpRequest.setHttpVersion(nextPartOfRequest.toString());
                    return;
                }

            }

            if (_byte != SP) {
                isNoReadSymbols = false;
                nextPartOfRequest.append((char) _byte);
            } else {

                if (isNextMethod) {
                    httpRequest.setMethod(nextPartOfRequest.toString());
                    nextPartOfRequest = new StringBuilder();
                    isNextMethod = false;
                    isNextRequestTarget = true;
                } else if (isNextRequestTarget) {

                    if (nextPartOfRequest.length() > 2000) {
                        throw new HttpParserException(HttpStatusCode.INTERNAL_SERVER_ERROR_500);
                    }

                    httpRequest.setRequestTarget(nextPartOfRequest.toString());
                    nextPartOfRequest = new StringBuilder();
                    isNextRequestTarget = false;
                }

            }

        }
    }

    /**
     * Метод парсящий headers http запроса
     *
     * @param reader      поток из которого читаются данные
     * @param httpRequest ?отпрашенный? запрос
     * @see HttpRequest#headers
     * @see HttpServerConfiguration#CRLF
     */
    private static void parseHeaders(InputStreamReader reader, HttpRequest httpRequest) throws IOException, HttpParserException {
        if (!reader.ready()) {
            throw new HttpParserException(HttpStatusCode.INTERNAL_SERVER_ERROR_500);
        }

        StringBuilder header = new StringBuilder();
        boolean secondIteration = false;
        int _byte;
        while ((_byte = reader.read()) >= 0) {

            if (_byte == CR) {

                if ((_byte = reader.read()) == LF) {

                    if (secondIteration) {
                        return;
                    }

                    secondIteration = true;
                    String[] nameAndValue = header.toString().split(":", 2);
                    httpRequest.headers.put(nameAndValue[0].trim(), nameAndValue[1].trim());
                    header = new StringBuilder();
                    continue;
                }

            }

            secondIteration = false;
            header.append((char) _byte);
        }
        return;
    }

    /**
     * Метод парсящий body http запроса
     *
     * @param reader      поток из которого читаются данные
     * @param httpRequest ?отпрашенный? запрос
     * @see HttpRequest#body
     */
    private static void parseBody(InputStreamReader reader, HttpRequest httpRequest) throws HttpParserException, IOException {
        if (!reader.ready()) {
            return;
        }

        StringBuilder httpRequestBody = new StringBuilder();
        int _byte;
        while ((_byte = reader.read()) >= 0) {
            httpRequestBody.append((char) _byte);
        }
        httpRequest.body = httpRequestBody.toString();
    }
}
