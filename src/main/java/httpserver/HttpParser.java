package httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Класс парсящий запрос поступивший на сервер
 * @author Кедровских Олег
 * @version 0.3
 */
public class HttpParser {
    private static final int SP = 32;
    private static final int CR = 13;
    private static final int LF = 10;

    /**
     * Метод парсящий запрос поступивший на сервер
     * @param inputStream - запрос
     * @return ?отпаршенный? запрос
     * @throws IOException - возникает при проблемах в InputStream с запросом
     */
    public static HttpRequest parseRequestLine(InputStream inputStream) throws IOException {
        HttpRequest request = new HttpRequest();
        InputStreamReader reader = new InputStreamReader(inputStream);
        parseRequestLine(reader, request);
        parseHeaders(reader, request);
        parseBody(reader, request);
        return request;
    }

    /**
     * Метод парсит request-line http запроса
     * @param reader - поток из которого читаются данные
     * @param request - ?отпрашенный? запрос
     * @throws IOException - возникает при ошибке чтения из потока
     */
    public static void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException {
        boolean method = false;
        boolean requestTarget = false;
        StringBuilder next = new StringBuilder();
        int _byte;
        while ((_byte = reader.read()) >= 0){
            if (_byte == CR){
                if ((_byte = reader.read()) == LF){
                    request.httpVersion = next.toString();
                    next.delete(0, next.length());
                    return;
                }
            }
            if (_byte != SP) {
                next.append((char) _byte);
            }
            else {
                if (!method){
                    request.method = next.toString();
                    next.delete(0, next.length());
                    method = true;
                }
                else if (!requestTarget){
                    request.requestTarget = next.toString();
                    next.delete(0, next.length());
                    requestTarget = true;
                }
            }
        }
    }

    /**
     * Метод парсящий headers http запроса
     * @param reader - поток из которого читаются данные
     * @param request - ?отпрашенный? запрос
     * @throws IOException - возникает при ошибке чтения из потока
     */
    private static void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean isKey = true;
        boolean secondIteration = false;
        int _byte;
        while ((_byte = reader.read()) >= 0){
            if (_byte == CR) {
                if ((_byte = reader.read()) == LF) {
                    if (secondIteration){
                        return;
                    }
                    request.headers.put(key.toString(), value.toString());
                    key.delete(0, key.length());
                    value.delete(0, value.length());
                    secondIteration = true;
                    isKey = true;
                    continue;
                }
            }
            secondIteration = false;
            if ((char)_byte == SP){
                isKey = false;
                continue;
            }
            if (isKey) {
                key.append((char) _byte);
            }
            else {
                value.append((char)_byte);
            }
        }
    }

    /**
     * Метод парсящий body http запроса
     * @param reader - поток из которого читаются данные
     * @param request - ?отпрашенный? запрос
     * @throws IOException - возникает при ошибке чтения из потока
     */
    private static void parseBody(InputStreamReader reader, HttpRequest request) throws IOException {
        StringBuilder bodyBuilder = new StringBuilder();
        int _byte;
        if (!reader.ready()){
            return;
        }
        while ((_byte = reader.read()) >= 0){
            bodyBuilder.append((char)_byte);
        }
        request.body = bodyBuilder.toString();
    }
}
