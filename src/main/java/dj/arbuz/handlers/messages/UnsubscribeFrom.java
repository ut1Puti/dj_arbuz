package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.database.local.UserStorage;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.Vk;
import dj.arbuz.user.BotUser;

import java.util.List;

public class UnsubscribeFrom implements MessageTelegramHandler {
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь быд отписан от группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#UNSUBSCRIBED
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder UNSUBSCRIBED = MessageHandlerResponse.newBuilder()
                                                                                                                   .textMessage(BotTextResponse.UNSUBSCRIBED);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не был подписчиком группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_SUBSCRIBER
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder NOT_SUBSCRIBER = MessageHandlerResponse.newBuilder()
                                                                                                                     .textMessage(BotTextResponse.NOT_SUBSCRIBER);
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder NOT_AUTHED_USER = MessageHandlerResponse.newBuilder()
                                                                                                                      .textMessage(BotTextResponse.NOT_AUTHED_USER);
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserStorage
     */
    private final UserBase usersBase;
    private final GroupBase groupsBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final Vk socialNetwork;

    public UnsubscribeFrom(GroupBase groupBase, UserBase usersBase, Vk vk) {
        this.groupsBase = groupBase;
        this.usersBase = usersBase;
        this.socialNetwork = vk;
    }
    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {
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
