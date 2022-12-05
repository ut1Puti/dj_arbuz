package dj.arbuz.handlers.messages;

import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработки команды /unsubscribe
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
@RequiredArgsConstructor
public final class UnsubscribeFromHandler extends DjArbuzAbstractMessageHandler {
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;
    /**
     * Поле хранилища групп, на которые оформлена подписка
     *
     * @see GroupBase
     */
    private final GroupBase groupsBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final AbstractVk socialNetwork;

    /**
     * Метод для отписывания пользователей от группы
     *
     * @param userReceivedGroupName название группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return ответ с сообщением о статусе отписки пользователя
     * @see AbstractVk#unsubscribeFrom(GroupBase, String, BotUser)
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    @Override
    public MessageHandlerResponse handleMessage(String userReceivedGroupName, String userSendResponseId) {
        if (userReceivedGroupName.isBlank()) {
            return createNoArgumentMessage("/unsubscribe", "название группы или исполнителя")
                    .build(List.of(userSendResponseId));
        }

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            boolean isUnsubscribed = socialNetwork.unsubscribeFrom(groupsBase, userReceivedGroupName, userCallingMethod);

            if (isUnsubscribed) {
                return UNSUBSCRIBED.build(List.of(userSendResponseId));
            }

            return NOT_SUBSCRIBER.build(List.of(userSendResponseId));
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(List.of(userSendResponseId));
        }
    }
}
