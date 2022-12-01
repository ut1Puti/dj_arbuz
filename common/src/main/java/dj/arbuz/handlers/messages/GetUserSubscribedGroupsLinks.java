package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.vk.Vk;
import dj.arbuz.socialnetworks.vk.VkConstants;
import dj.arbuz.user.BotUser;

import java.util.List;
import java.util.Set;

public class GetUserSubscribedGroupsLinks implements MessageTelegramHandler {
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не аутентифицировался в социальной сети
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_AUTHED_USER
     */
    private static final MessageHandlerResponseBuilder NOT_AUTHED_USER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_AUTHED_USER);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не был подписчиком группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_SUBSCRIBER
     */
    private static final MessageHandlerResponseBuilder NOT_SUBSCRIBER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_SUBSCRIBER);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не подписан ни на одну группу
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_SUBSCRIBED_GROUPS
     */
    private static final MessageHandlerResponseBuilder NO_SUBSCRIBED_GROUPS = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_SUBSCRIBED_GROUPS);

    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;
    private final GroupBase groupsBase;

    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    public GetUserSubscribedGroupsLinks(UserBase usersBase, GroupBase groupsBase) {
        this.usersBase = usersBase;
        this.groupsBase = groupsBase;
    }

    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);
        Set<String> userSubscribedGroupsName = groupsBase.getUserSubscribedGroups(userCallingMethod.getTelegramId());

        if (userSubscribedGroupsName.isEmpty()) {
            return NO_SUBSCRIBED_GROUPS.build(List.of(userSendResponseId));
        }

        StringBuilder userSubscribedGroupsLinks = new StringBuilder();
        userSubscribedGroupsLinks.append("Ваши подписки:\n");
        int counter = 1;
        for (String userSubscribedGroupName : userSubscribedGroupsName) {
            userSubscribedGroupsLinks.append(Integer.toString(counter))
                                     .append(") [")
                                     .append(userSubscribedGroupName)
                                     .append("]")
                                     .append("(")
                                     .append(VkConstants.VK_ADDRESS)
                                     .append(userSubscribedGroupName)
                                     .append(")")
                                     .append("\n");
            counter++;
        }
        return MessageHandlerResponse.newBuilder()
                                     .textMessage(userSubscribedGroupsLinks.toString())
                                     .build(List.of(userSendResponseId));
    }
}
