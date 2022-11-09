package handlers.messages;

import bots.StoppableByUser;

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
     * @param stoppableByUserThread   поток бота, из которого был получен запрос
     * @return ответ бота на сообщение пользователя
     */
    MessageHandlerResponse handleMessage(String message, String userIdInBot, StoppableByUser stoppableByUserThread);
}
