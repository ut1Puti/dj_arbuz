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
    /** Поле пробела из таблицы ASCII */
    private static final int SP = 32;
    /** Поле \r из таблицы ASCII */
    private static final int CR = 13;
    /** Поле \n из таблицы ASCII */
    private static final int LF = 10;
    /** поле поддерживаемых методов */
    private static final String[] supportedMethods = new String[] {"GET", "HEAD"};

    /**
     * Метод парсящий запрос поступивший на сервер
     * @param inputStream - запрос
     * @return ?отпаршенный? запрос
     * @throws IOException - возникает при проблемах в InputStream с запросом
     */
    public static HttpRequest parseRequestLine(InputStream inputStream) throws IOException, HttpParserException {
        InputStreamReader reader = new InputStreamReader(inputStream);

        HttpRequest request = new HttpRequest();
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
     * @throws HttpParserException - возникает при ошибках в запросе
     */
    public static void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParserException {
        boolean isNextMethod = true;
        boolean isNextRequestTarget = false;
        StringBuilder nextPartOfRequest = new StringBuilder();

        if (!reader.ready()){
            throw new HttpParserException("Incorrect request");
        }

        int _byte;
        while ((_byte = reader.read()) >= 0) {

            if (_byte == CR) {

                if ((_byte = reader.read()) == LF) {

                    if (isNextMethod || isNextRequestTarget) {
                        throw new HttpParserException("Incorrect request");
                    }

                    request.httpVersion = nextPartOfRequest.toString();
                    nextPartOfRequest.delete(0, nextPartOfRequest.length());
                    return;
                }

            }

            if (_byte != SP) {
                nextPartOfRequest.append((char) _byte);
            } else {

                if (isNextMethod) {
                    boolean wasMethodFound = false;
                    for (String method : supportedMethods){

                        if (method.equals(nextPartOfRequest.toString())){
                            wasMethodFound = true;
                            break;
                        }

                    }

                    if (!wasMethodFound){
                        throw new HttpParserException("Incorrect request");
                    }

                    request.method = nextPartOfRequest.toString();
                    nextPartOfRequest.delete(0, nextPartOfRequest.length());
                    isNextMethod = false;
                    isNextRequestTarget = true;
                } else if (isNextRequestTarget) {

                    if (nextPartOfRequest.length() > 2000){
                        throw new HttpParserException("Incorrect request");
                    }

                    request.requestTarget = nextPartOfRequest.toString();
                    nextPartOfRequest.delete(0, nextPartOfRequest.length());
                    isNextRequestTarget = false;
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
    private static void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParserException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean isKey = true;
        boolean secondIteration = false;

        if (!reader.ready()){
            throw new HttpParserException("Incorrect request");
        }

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                if ((_byte = reader.read()) == LF) {

                    if (secondIteration) {
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

            if ((char)_byte == SP) {
                isKey = false;
                continue;
            }

            if (isKey) {
                key.append((char) _byte);
            } else {
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

        if (!reader.ready()) {
            return;
        }

        while ((_byte = reader.read()) >= 0) {
            bodyBuilder.append((char)_byte);
        }
        request.body = bodyBuilder.toString();
    }
}
