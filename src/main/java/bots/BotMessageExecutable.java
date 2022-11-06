package bots;

/**
 * Интерфейс для отправки сообщений ботом
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface BotMessageExecutable {
    /**
     * Метод отправляющий сообщение пользователю
     *
     * @param userSendResponseId  id пользователя, которому будет отправлено сообщение, в системе
     * @param responseSendMessage текст сообщения, который должен быть отправлен пользователю
     */
    void send(String userSendResponseId, String responseSendMessage);
}
