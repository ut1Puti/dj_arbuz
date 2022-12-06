package httpserver.message.request;

import httpserver.messages.request.HttpMethod;
import httpserver.messages.request.HttpRequest;
import httpserver.parser.HttpParserException;
import httpserver.messages.HttpStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Класс тестирующий формирование request'а
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpRequestTests {
    /**
     * Поле http request'а
     */
    private HttpRequest request;

    /**
     * Метод создающий http request перед каждым тестом
     */
    @BeforeEach
    public void setUpRequest() {
       request = new HttpRequest();
    }

    /**
     * Метод тестирующий обработку поддерживаемого метода
     */
    @Test
    public void testSupportedMethod() throws HttpParserException {
        String method = "GET";
        request.setMethod(method);
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    /**
     * Метод тестирующий обработку неподдерживаемого метода
     */
    @Test
    public void testUnSupportedMethod() {
        String method = "POST";
        try {
            request.setMethod(method);
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.NOT_IMPLEMENTED_501.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }

    /**
     * Метод тестирующий обработку метода, которого нет в http
     */
    @Test
    public void testNotHttpMethod() {
        String method = "RUN";
        try {
            request.setMethod(method);
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.NOT_IMPLEMENTED_501.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }
}
