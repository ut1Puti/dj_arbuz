package dj.arbuz.httpserver.parser;

/**
 * Класс ошибки парсинга http запроса
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpParserException extends Exception {
    /**
     * Конструктор - создает экземпляр класса с сообщением
     *
     * @param message - сообщение ошибки
     */
    public HttpParserException(String message) {
        super(message);
    }

    /**
     * Конструктор - создает экземпляр класса с сообщением об ошибкой и самой ошибкой
     *
     * @param errorMessage - сообщение ошибки
     * @param err          - ошибка
     */
    public HttpParserException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    /**
     * Конструктор - создает экземлер класаа с ошибкой
     *
     * @param err - ошибка
     */
    public HttpParserException(Throwable err) {
        super(err);
    }
}
