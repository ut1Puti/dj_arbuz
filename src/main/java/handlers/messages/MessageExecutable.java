package handlers.messages;

import database.UserStorage;

/**
 * Интерфейс для отправки сообщений обработчика пользователям
 *
 * @author Кедровских Олег
 * @version 1.0
 */
interface MessageExecutable {
    /**
     * Метод для отправки сообщений пользователям
     *
     * @param response ответ бота, который необходимо отправить пользователю
     * @param userBase база данных пользователей
     */
    void executeMessage(MessageHandlerResponse response, UserStorage userBase);
}
