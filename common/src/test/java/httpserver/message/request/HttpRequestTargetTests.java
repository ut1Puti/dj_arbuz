package httpserver.message.request;

import httpserver.messages.request.HttpRequestTarget;
import httpserver.parser.HttpParserException;
import httpserver.messages.HttpStatusCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Класс тестирующий создание http request-target
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpRequestTargetTests {
    /**
     * Метод тестирующий создание target-request при наличии параметров в запросе
     */
    @Test
    public void testRequestTargetWithParameters() throws HttpParserException {
        String requestedFile = "/file";
        String parameters = "param";
        HttpRequestTarget target = new HttpRequestTarget(requestedFile + "?" + parameters);
        assertEquals(requestedFile, target.getRequestTargetFile());
        assertEquals(parameters, target.getParameters());
    }

    /**
     * Метод тестирующий создание request-target без параметров
     */
    @Test
    public void testRequestTargetWithoutParameters() throws HttpParserException {
        String requestedFile = "/file";
        HttpRequestTarget target = new HttpRequestTarget(requestedFile);
        assertEquals(requestedFile, target.getRequestTargetFile());
        assertEquals("", target.getParameters());
    }

    /**
     * Метод тестирующий обработку ошибки пустой строки
     */
    @Test
    public void testRequestTargetEmptyString() {
        try {
            new HttpRequestTarget("");
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.BAD_REQUEST_400.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }

    /**
     * Метод тестирующий обработку ошибку передачи null в качестве цели
     */
    @Test
    public void testRequestTargetNull() {
        try {
            new HttpRequestTarget(null);
        } catch (HttpParserException e) {
            assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR_500.getCodeMessage(), e.getMessage());
            return;
        }
        fail();
    }
}
