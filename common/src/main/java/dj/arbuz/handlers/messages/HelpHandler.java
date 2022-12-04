package dj.arbuz.handlers.messages;

import java.util.List;

/**
 * Класс команды /help
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
public final class HelpHandler extends DjArbuzAbstractMessageHandler {
    /**
     * Метод для обработки команды /help пользователем
     *
     * @param message     сообщение пользователя
     * @param userSendResponseId id пользователя в системе бота
     * @return ответ бота на сообщение пользователя /help
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        if (message != null) {
            return createIllegalArgumentMessage("/help", message).build(List.of(userSendResponseId));
        }

        return HELP_INFO.build(List.of(userSendResponseId));
    }
}
