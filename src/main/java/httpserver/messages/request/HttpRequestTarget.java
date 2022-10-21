package httpserver.messages.request;

import httpserver.parser.HttpParserException;
import httpserver.parser.HttpStatusCode;

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
     * @see HttpStatusCode#SERVER_ERROR_500_INTERNAL_SERVER_ERROR
     * @see HttpStatusCode#CLIENT_ERROR_400_BAD_REQUEST
     */
    public HttpRequestTarget(String requestTargetReceiverFromRequest) throws HttpParserException {
        if (requestTargetReceiverFromRequest == null) {
            throw new HttpParserException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }

        String[] split = requestTargetReceiverFromRequest.split("\\?", 2);

        if (!split[0].contains("/")) {
            throw new HttpParserException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        this.requestTargetFile = split[0];

        if (split.length == 2) {
            this.parameters = split[1];
        } else {
            this.parameters = "";
        }
    }

    /**
     * Метод получающий цель запроса
     *
     * @return цель запроса в виде имени файла
     */
    public String getRequestTargetFile() {
        return requestTargetFile;
    }

    /**
     * Метод получающий параметры отправленные с файлом
     *
     * @return параметры отправленные с файлом
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Метод проверяющий равен ли экземпляр класса некоторому объекту
     *
     * @param obj - некоторый объект
     * @return true - если равны, false - если не равны
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        return requestTargetFile.equals(((HttpRequestTarget) obj).requestTargetFile) &&
                parameters.equals(((HttpRequestTarget) obj).parameters);
    }
}
