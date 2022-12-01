package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.Vk;
import dj.arbuz.user.BotUser;

import java.util.List;

public class SubsribeTo implements MessageTelegramHandler{
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


    public SubsribeTo(GroupBase groupBase,UserBase usersBase, Vk vk) {
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
