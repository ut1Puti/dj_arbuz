package dj.arbuz.handlers.messages;

import java.util.List;

/**
 * Класс для обработки неизвестной команды
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class UnknownCommand extends DjArbuzAbstractMessageHandler {
    /**
     * Метод для обработки сообщения пользователем
     *
     * @param message     сообщение пользователя
     * @param userSendResponseId id пользователя в системе бота
     * @return ответ бота на сообщение пользователя
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        return UNKNOWN_COMMAND.build(List.of(userSendResponseId));
    }
}
