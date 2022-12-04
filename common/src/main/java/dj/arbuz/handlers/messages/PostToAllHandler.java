package dj.arbuz.handlers.messages;

import dj.arbuz.database.UserBase;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработчика команды /post_to_all
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@RequiredArgsConstructor
public final class PostToAllHandler extends DjArbuzAbstractMessageHandler {
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;

    /**
     * Метод для обработки команды /post_to_all
     *
     * @param message     сообщение пользователя
     * @param userReceivedMessageId id пользователя в системе бота
     * @return ответ бота на сообщение пользователя
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userReceivedMessageId) {
        if (!usersBase.isAdmin(userReceivedMessageId)) {
            return UNKNOWN_COMMAND.build(List.of(userReceivedMessageId));
        }

        if (message == null) {
            return createNoArgumentMessage("/post_to_all", "сообщение в виде строки, которое будет отправлено всем пользователям")
                    .build(List.of(userReceivedMessageId));
        }

        return MessageHandlerResponse.newBuilder().textMessage(message).build(usersBase.getAllUsersId());
    }
}
