package dj.arbuz.handlers.messages;

/**
 * Интерфейс для обработки сообщений бота, полученные от пользователя
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface MessageHandler {
    /**
     * Метод для обработки сообщения пользователем
     *
     * @param message     сообщение пользователя
     * @param userIdInBot id пользователя в системе бота
     * @return ответ бота на сообщение пользователя
     */
    MessageHandlerResponse handleMessage(String message, String userIdInBot);
}
