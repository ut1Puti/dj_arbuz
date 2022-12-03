package dj.arbuz.handlers.messages;

import dj.arbuz.database.GroupBase;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработки команды /post_to_group
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@RequiredArgsConstructor
public class PostToGroup extends DjArbuzAbstractMessageHandler {
    /**
     * Поле хранилища групп, на которые оформлена подписка
     *
     * @see GroupBase
     */
    private final GroupBase groupsBase;

    /**
     * Метод обработки команды /post_to_group
     *
     * @param message     сообщение пользователя
     * @param userReceivedMessageId id пользователя в системе бота
     * @return ответ бота на сообщение пользователя
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userReceivedMessageId) {
        if (message == null) {
            return createNoArgumentMessage("/post_to_group", "строка с сообщением, которое будет отправлено подписчикам")
                    .build(List.of(userReceivedMessageId));
        }

        String[] groupNameAndPostText = message.split(" ", 2);

        if (!groupsBase.isGroupAdmin(groupNameAndPostText[0], userReceivedMessageId)) {
            return IS_NOT_ADMIN.build(List.of(userReceivedMessageId));
        }

        if (!groupsBase.containsGroup(groupNameAndPostText[0])) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(new NoGroupException(groupNameAndPostText[0]).getMessage())
                    .build(List.of(userReceivedMessageId));
        }

        return MessageHandlerResponse.newBuilder()
                .textMessage(groupNameAndPostText[1])
                .build(groupsBase.getSubscribedToGroupUsersId(groupNameAndPostText[1]));
    }
}
