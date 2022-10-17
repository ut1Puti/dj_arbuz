package httpserver.parser;

import httpserver.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Класс тестирующий парсер http запросов
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpParserTests {
    /**
     * Метод тестирующий корректную обработку верного http запроса
     *
     * @throws HttpParserException - возникает при некоректном http запросе
     * @throws IOException - возникает при ошибке чтения входного потока
     */
    @Test
    public void testCorrectHttpRequest() throws HttpParserException, IOException {
        String httpRequest = "GET /redirect.html?code /HTTP/1.1\r\n" +
                "Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r\n" +
                "User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r\n" +
                "Host: localhost:8080\r\n" + "Connection: keep-alive\r\n\r\n";
        InputStream is = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = HttpParser.parseRequestLine(is);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept:", "text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2");
        headers.put("User-Agent:", "Jersey/2.35(HttpUrlConnection18.0.2.1)");
        headers.put("Host:", "localhost:8080");
        headers.put("Connection:", "keep-alive");
        assertEquals("GET", request.method);
        assertEquals("/redirect.html?code", request.requestTarget);
        assertEquals("/HTTP/1.1", request.httpVersion);
        assertEquals(headers, request.headers);
        assertNull(request.body);
    }

    /**
     * Метод тестирующий обработку некоректного или неподдерживаемого серером метода в http запросе
     */
    @Test
    public void testIncorrectMethodInHttpRequest() {
        String httpRequest = "POST /redirect.html?code /HTTP/1.1\r\n" +
                "Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r\n" +
                "User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r\n" +
                "Host: localhost:8080\r\n" + "Connection: keep-alive\r\n\r\n";
        InputStream is = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            HttpRequest request = HttpParser.parseRequestLine(is);
        } catch (IOException ignored) {
        } catch (HttpParserException e) {
            assertEquals("Incorrect request", e.getMessage());
            return;
        }
        throw new RuntimeException("Тест не пройден, тк не было поймано исключение");
    }
}
