package httpserver.parser;

import httpserver.message.request.HttpVersionTests;
import httpserver.messages.request.HttpMethod;
import httpserver.messages.request.HttpRequest;
import httpserver.messages.request.HttpRequestTarget;
import httpserver.messages.request.HttpVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
     * @throws HttpParserException - возникает при некорректном http запросе
     * @throws IOException         - возникает при ошибке чтения входного потока
     */
    @Test
    public void testCorrectHttpRequest() throws HttpParserException, IOException {
        String httpRequest = """
                GET /redirect.html?code HTTP/1.1\r
                Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r
                User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                \r
                """;
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = HttpParser.parseRequest(inputStream);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2");
        headers.put("User-Agent", "Jersey/2.35(HttpUrlConnection18.0.2.1)");
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals(new HttpRequestTarget("/redirect.html?code"), request.getRequestTarget());
        assertEquals("HTTP/1.1", request.getOriginalHttpVersion());
        assertEquals(HttpVersion.HTTP_1_1, request.getBestCompatibleHttpVersion());
        assertEquals(headers, request.headers);
        assertEquals("", request.body);
    }

    /**
     * Метод тестирующий обработку некорректных CRLF в запросе
     *
     * @throws IOException - возникает при ошибке чтения входного потока
     */
    @Test
    public void testIncorrectCRLFsInHttpRequest() throws IOException {
        String httpRequest = """
                GET /redirect.html?code HTTP/1.1\r
                Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r
                User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r
                Host: localhost:8080
                Connection: keep-alive\r
                \r
                """;
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            HttpParser.parseRequest(inputStream);
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }

    /**
     * Метод тестирующий обработку некорректного или неподдерживаемого сервером метода в http запросе
     */
    @Test
    public void testIncorrectMethodInHttpRequest() {
        String httpRequest = """
                POST /redirect.html?code HTTP/1.1\r
                Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r
                User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                \r
                """;
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            HttpParser.parseRequest(inputStream);
        } catch (IOException ignored) {
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }

    /**
     * Метод тестирующий обработку некорректную цель запроса
     */
    @Test
    public void testNoRequestTargetInHttpRequest() {
        String httpRequest = """
                GET HTTP/1.1\r
                Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r
                User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                \r
                """;
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            HttpParser.parseRequest(inputStream);
        } catch (IOException ignored) {
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }

    /**
     * Метод тестирующий обработку исключения некорректной версии http
     */
    @Test
    public void testWrongHttpVersionInHttpRequest() {
        String httpRequest = """
                GET /redirectUrl HTTP/1.\r
                Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r
                User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                \r
                """;
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            HttpParser.parseRequest(inputStream);
        } catch (IOException ignored) {
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }

    /**
     * Метод тестирующий обработку версии до поддерживаемой
     */
    @ParameterizedTest(name = "Версия http из запроса {0} : Лучшая совместимая {1}")
    @MethodSource("supportedVersionsData")
    public void testHttpVersionMoreThanSupported(String httpVersion, HttpVersion expectedBestCompatibleHttpVersion)
            throws IOException, HttpParserException {
        String httpRequest = """
                GET /redirect.html?code {version}\r
                Accept: text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2\r
                User-Agent: Jersey/2.35(HttpUrlConnection18.0.2.1)\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                \r
                """.replace("{version}", httpVersion);
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = HttpParser.parseRequest(inputStream);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,image/gif,image/jpeg,*;q=.2,*/*;q=.2");
        headers.put("User-Agent", "Jersey/2.35(HttpUrlConnection18.0.2.1)");
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals(new HttpRequestTarget("/redirect.html?code"), request.getRequestTarget());
        assertEquals(httpVersion, request.getOriginalHttpVersion());
        assertEquals(expectedBestCompatibleHttpVersion, request.getBestCompatibleHttpVersion());
        assertEquals(headers, request.headers);
        assertEquals("", request.body);
    }

    /**
     * Метод создающий данные для теста совместимых версий
     *
     * @return stream данных для теста
     */
    private static Stream<Arguments> supportedVersionsData() {
        return HttpVersionTests.testCompatibleVersionsData();
    }
}
