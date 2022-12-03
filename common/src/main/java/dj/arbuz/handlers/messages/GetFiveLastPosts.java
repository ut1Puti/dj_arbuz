package dj.arbuz.handlers.messages;

import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Класс обработки команды /five_posts
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
@RequiredArgsConstructor
public class GetFiveLastPosts extends DjArbuzAbstractMessageHandler {
    /**
     * Поле кол-ва постов отправляемых пользователю
     */
    private static final int DEFAULT_POST_NUMBER = 5;
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

    /**
     * Метод возвращающий ответ на ответ на /get_last_posts
     *
     * @param userReceivedGroupName имя группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return текст постов, ссылки на изображения в них, а также ссылки
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#postsText(List)
     */
    @Override
    public MessageHandlerResponse handleMessage(String userReceivedGroupName, String userSendResponseId) {
        if (userReceivedGroupName == null) {
            return createNoArgumentMessage("/five_posts", "название группы или исполнителя")
                    .build(List.of(userSendResponseId));
        }


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
