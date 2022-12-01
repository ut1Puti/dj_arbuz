package dj.arbuz.handlers.messages;
<<<<<<<< HEAD:common/src/main/java/dj/arbuz/handlers/messages/MessageHandler.java
========

import dj.arbuz.bots.StoppableByUser;
>>>>>>>> developTaskFour:src/main/java/dj/arbuz/handlers/messages/MessageHandler.java

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
