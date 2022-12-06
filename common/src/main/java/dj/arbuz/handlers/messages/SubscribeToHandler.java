package dj.arbuz.handlers.messages;

import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработки команды /subscribe
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
@RequiredArgsConstructor
public final class SubscribeToHandler extends DjArbuzAbstractMessageHandler {
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
     * @see AbstractVk
     */
    private final AbstractVk socialNetwork;

    /**
     * Метод для подписки пользователя
     *
     * @param userReceivedGroupName Название группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return возвращает ответ содержащий информацию о статусе подписки пользователя
     * @see AbstractVk#subscribeTo(GroupBase, String, BotUser)
     * @see dj.arbuz.socialnetworks.socialnetwork.groups.SubscribeStatus
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    @Override
    public MessageHandlerResponse handleMessage(String userReceivedGroupName, String userSendResponseId) {
        if (userReceivedGroupName.isBlank()) {
            return createNoArgumentMessage("/subscribe", "название группы или исполнителя")
                    .build(List.of(userSendResponseId));
        }


        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(socialNetwork.subscribeTo(groupsBase, userReceivedGroupName, userCallingMethod).getSubscribeMessage())
                    .build(List.of(userSendResponseId));
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(List.of(userSendResponseId));
        }
    }
}
