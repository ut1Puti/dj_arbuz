package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.database.UserBase;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработчика команды /stop
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@RequiredArgsConstructor
public final class StopHandler extends DjArbuzAbstractMessageHandler {
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;

    /**
     * Метод обрабатывающий команду /stop
     *
     * @param message     сообщение пользователя
     * @param userReceivedMessageId id пользователя в системе бота
     * @return ответ
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userReceivedMessageId) {
        if (message != null) {
            return createIllegalArgumentMessage("/stop", message).build(List.of(userReceivedMessageId));
        }

        usersBase.deleteUser(userReceivedMessageId);
        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.STOP_INFO)
                .build(List.of(userReceivedMessageId));
    }
}
