package dj.arbuz;

import dj.arbuz.handlers.messages.MessageHandlerResponse;

/**
 * Интерфейс для отправки сообщений пользователям
 *
 * @author Кедровских Олег
 * @version 1.0
 */
interface MessageSender {
    /**
     * Метод для отправки ответа обработчика
     *
     * @param userSendResponse ответ обработчика
     */
    void sendResponse(MessageHandlerResponse userSendResponse);

    /**
     * Метод для отправки одного текстового сообщения пользователю
     *
     * @param userSendResponseId id пользователя в системе
     * @param userSendText текстовое сообщение, которое будет отправлено пользователю
     */
    void sendSingleMessage(String userSendResponseId, String userSendText);
}
