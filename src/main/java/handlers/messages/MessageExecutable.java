package handlers.messages;

import bots.StoppableByUser;

/**
 * Интерфейс для обработки сообщений бота, полученные от пользователя
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface MessageExecutable {
    /**
     * Метод для обработки сообщения пользователем
     *
     * @param message     сообщение пользователя
     * @param userIdInBot id пользователя в системе бота
     * @param botThread   поток бота, из которого был получен запрос
     * @return ответ бота на сообщение пользователя
     */
    MessageExecutorResponse executeMessage(String message, String userIdInBot, StoppableByUser botThread);
}
