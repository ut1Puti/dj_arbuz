package httpserver.parser;

/**
 * Класс ошибки парсинга http запроса
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpParserException extends Exception {
    /**
     * Поле кода ошибки
     */
    private final HttpStatusCode errorCode;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param errorCode - код ошибки
     */
    public HttpParserException(HttpStatusCode errorCode) {
        super(errorCode.getCodeMessage());
        this.errorCode = errorCode;
    }

    /**
     * Метод получающий информацию об ошибке
     *
     * @return информацию об ошибке
     */
    public HttpStatusCode getErrorCode() {
        return errorCode;
    }
}
