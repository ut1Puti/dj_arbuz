package httpserver.messages.request;

import httpserver.messages.HttpMessage;
import httpserver.parser.HttpParserException;
import httpserver.parser.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс http запросов
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpRequest extends HttpMessage {
    /**
     * Поле запрошенного метода
     */
    private HttpMethod method;
    /**
     * Поле запрошенной цели
     */
    private HttpRequestTarget requestTarget;
    /**
     * Поле версии в http запросе
     */
    private String originalHttpVersion;
    /**
     * Поле лучшей совместимой версии, с этой версией будет отправлен ответ
     */
    private HttpVersion bestCompatibleHttpVersion;
    /**
     * Поле headers
     */
    public Map<String, String> headers = new HashMap<>();
    /**
     * Поле body
     */
    public String body = "";

    /**
     * Метод получающий метод из запроса
     *
     * @return енум метода из запроса
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Метод устанавливающий метод
     *
     * @param methodName - название метода в виде строки
     * @throws HttpParserException - возникает при отсутствии реализации полученного метода
     */
    public void setMethod(String methodName) throws HttpParserException {
        for (HttpMethod method : HttpMethod.values()) {

            if (methodName.equals(method.name())) {
                this.method = method;
                return;
            }

        }
        throw new HttpParserException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }

    /**
     * Метод получающий весь таргет запроса
     *
     * @return таргет запроса
     */
    public HttpRequestTarget getRequestTarget() {
        return requestTarget;
    }

    /**
     * Метод устанавливающий новый таргет запроса
     *
     * @param requestTargetFromHttpRequest - таргет из запроса
     * @throws HttpParserException - возникает при невозможности сформировать таргет по таргету из запроса
     */
    public void setRequestTarget(String requestTargetFromHttpRequest) throws HttpParserException {
        requestTarget = new HttpRequestTarget(requestTargetFromHttpRequest);
    }

    /**
     * Метод получающий http версию из полученного запроса
     *
     * @return http версию из запроса
     */
    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    /**
     * Метод получающий совместимую версию
     *
     * @return совместимую версию для сервера и запроса
     */
    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleHttpVersion;
    }

    /**
     * Метод устанавливающий версию http, совместимую с версией сервера и отправителя
     *
     * @param requestHttpVersion - полученная версия http
     * @throws HttpParserException - ошибка парсера, возникает если версия в полученном запросе некорректна
     */
    public void setHttpVersion(String requestHttpVersion) throws HttpParserException {
        originalHttpVersion = requestHttpVersion;
        bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(requestHttpVersion);

        if (bestCompatibleHttpVersion == null) {
            throw new HttpParserException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }

    }
}
