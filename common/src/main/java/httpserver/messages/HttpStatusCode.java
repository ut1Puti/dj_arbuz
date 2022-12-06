package httpserver.messages;

/**
 * Перечисление кодов http ответов сервера
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public enum HttpStatusCode {
    /**
     * Значения ответа
     */
    OK_200(200, "Ok"),
    NOT_FOUND_404(404, "Not found"),
    /**
     * Значения ошибок клиента
     */
    BAD_REQUEST_400(400, "Bad Request"),
    /**
     * Значения ошибок сервера
     */
    INTERNAL_SERVER_ERROR_500(500, "Internal Server Error"),
    NOT_IMPLEMENTED_501(501, "Not Implemented"),
    HTTP_VERSION_NOT_SUPPORTED_505(505, "Http Version Not Supported");

    /**
     * Поле кода ошибки
     */
    private final int statusCode;
    /**
     * Поле сообщения ошибки
     */
    private final String codeMessage;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param statusCode  - код ошибки
     * @param codeMessage - сообщение ошибки
     */
    HttpStatusCode(int statusCode, String codeMessage) {
        this.statusCode = statusCode;
        this.codeMessage = codeMessage;
    }

    /**
     * Метод получающий код ошибки
     *
     * @return код ошибки
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Метод получающий сообщение ошибки
     *
     * @return сообщение ошибки
     */
    public String getCodeMessage() {
        return codeMessage;
    }
}
