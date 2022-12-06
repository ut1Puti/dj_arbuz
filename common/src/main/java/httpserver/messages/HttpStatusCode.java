package httpserver.messages;

/**
 * Енум ошибок http сервера
 *
 * @author Кедровсикх Олег
 * @version 1.0
 */
public enum HttpStatusCode {
    /**
     * Значения ответа
     */
    OK(200, "Ok"),
    NOT_FOUND(404, "Not found"),
    /**
     * Значения ошибок клиента
     */
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    /**
     * Значения ошибок сервера
     */
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "Http Version Not Supported");

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
