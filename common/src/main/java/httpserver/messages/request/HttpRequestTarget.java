package httpserver.messages.request;

import httpserver.parser.HttpParserException;
import httpserver.messages.HttpStatusCode;

import java.util.Objects;

/**
 * Класс для хранения таргетов http запроса
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpRequestTarget {
    /**
     * Поле хранящее цель запроса в виде файла или исполняемого файла
     */
    private final String requestTargetFile;
    /**
     * Поле хранящее параметры отправленные с целью запроса
     */
    private final String parameters;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param requestTargetReceiverFromRequest - таргет отправленный на сервер
     * @throws HttpParserException - возникает при ошибке формирования таргета из запроса
     * @see HttpStatusCode#INTERNAL_SERVER_ERROR_500
     * @see HttpStatusCode#BAD_REQUEST_400
     */
    public HttpRequestTarget(String requestTargetReceiverFromRequest) throws HttpParserException {
        if (requestTargetReceiverFromRequest == null) {
            throw new HttpParserException(HttpStatusCode.INTERNAL_SERVER_ERROR_500);
        }

        String[] split = requestTargetReceiverFromRequest.split("\\?", 2);

        if (!split[0].contains("/")) {
            throw new HttpParserException(HttpStatusCode.BAD_REQUEST_400);
        }

        this.requestTargetFile = split[0];

        if (split.length == 2) {
            this.parameters = split[1];
        } else {
            this.parameters = "";
        }
    }

    /**
     * Метод получающий {@code requestTargetFile}
     *
     * @return цель запроса в виде имени файла
     */
    public String getTargetFile() {
        return requestTargetFile;
    }

    /**
     * Метод получающий {@code parameters}
     *
     * @return параметры отправленные с файлом
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Метод вычисляющий хэш экземляра класса
     *
     * @return хэш экземпляра
     */
    @Override
    public int hashCode() {
        return Objects.hash(requestTargetFile, parameters);
    }

    /**
     * Метод проверяющий равен ли {@code HttpRequestTarget} {@code obj}
     *
     * @param obj - некоторый объект
     * @return true - если равны, false - если не равны
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof HttpRequestTarget anotherHttpRequestTarget)) {
            return false;
        }

        return requestTargetFile.equals(anotherHttpRequestTarget.requestTargetFile) &&
                parameters.equals(anotherHttpRequestTarget.parameters);
    }
}
