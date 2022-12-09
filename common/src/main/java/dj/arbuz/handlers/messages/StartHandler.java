package dj.arbuz.handlers.messages;

import java.util.List;

/**
 * Класс для обработки команды /start
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class StartHandler extends DjArbuzAbstractMessageHandler{
    /**
     * Метод для обработки сообщения пользователем
     *
     * @param message     сообщение пользователя
     * @param userSendResponseId id пользователя в системе бота
     * @return ответ бота на сообщение пользователя
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        if (!message.isBlank()) {
            return createIllegalArgumentMessage("/start", message).build(List.of(userSendResponseId));
        }

        return START.build(List.of(userSendResponseId));
    }
}
