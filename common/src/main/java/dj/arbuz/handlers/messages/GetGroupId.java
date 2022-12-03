package dj.arbuz.handlers.messages;

import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.AbstractSocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработки команды /id
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
@RequiredArgsConstructor
public class GetGroupId extends DjArbuzAbstractMessageHandler {
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see AbstractVk
     */
    private final AbstractVk socialNetwork;

    /**
     * Метод возвращающий ответ на /id
     *
     * @param userReceivedGroupName имя группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return id верифицированной группы если такая нашлась
     * @see AbstractSocialNetwork#getGroupId(String, Object)
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    @Override
    public MessageHandlerResponse handleMessage(String userReceivedGroupName, String userSendResponseId) {
        if (userReceivedGroupName == null) {
            return createNoArgumentMessage("/id", "название группы или исполнителя")
                    .build(List.of(userSendResponseId));
        }

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(socialNetwork.getGroupId(userReceivedGroupName, userCallingMethod))
                    .build(List.of(userSendResponseId));
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(List.of(userSendResponseId));
        }

    }
}
