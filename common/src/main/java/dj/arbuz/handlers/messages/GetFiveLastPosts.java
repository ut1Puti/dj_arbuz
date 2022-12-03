package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;

import dj.arbuz.database.UserBase;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;

import java.util.List;
import java.util.NoSuchElementException;

public class GetFiveLastPosts  implements MessageTelegramHandler{
    private static final int DEFAULT_POST_NUMBER = 5;
    /**
     * Поле сообщения с текстом, в котором говориться, что не нашлось постов в группе
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_POSTS_IN_GROUP
     */
    private static final MessageHandlerResponseBuilder NO_POSTS_IN_GROUP = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_POSTS_IN_GROUP);
    private static final MessageHandlerResponseBuilder NOT_AUTHED_USER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_AUTHED_USER);
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final AbstractVk socialNetwork;
    public GetFiveLastPosts(UserBase usersBase, AbstractVk vk) {
        this.usersBase = usersBase;
        this.socialNetwork = vk;
    }
    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        List<String> groupFindPosts;
        try {
            groupFindPosts = socialNetwork.getLastPostsAsStrings(userReceivedGroupName, DEFAULT_POST_NUMBER, userCallingMethod);
        } catch (NoSuchElementException e) {
            return NO_POSTS_IN_GROUP.build(List.of(userSendResponseId));
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                                         .textMessage(e.getMessage())
                                         .build(List.of(userSendResponseId));
        }

        if (groupFindPosts.isEmpty()) {
            return NO_POSTS_IN_GROUP.build(List.of(userSendResponseId));
        }

        return MessageHandlerResponse.newBuilder()
                                     .postsText(groupFindPosts)
                                     .build(List.of(userSendResponseId));
    }
}
