package httpserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс http запросов
 *
 * @author Кедровских Олег
 * @version 0.1
 */
public class HttpRequest {
    /**
     * Поле запрошенного метода
     */
    public String method;
    /**
     * Поле запрошенной цели
     */
    public String requestTarget;
    /**
     * Поле версии http
     */
    public String httpVersion;
    /**
     * Поле headers
     */
    public Map<String, String> headers = new HashMap<>();
    /**
     * Поле body
     */
    public String body = null;

    /**
     * Констурктор
     */
    public HttpRequest() {

    }
}
