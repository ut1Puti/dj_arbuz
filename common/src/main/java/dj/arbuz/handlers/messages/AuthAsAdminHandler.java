package dj.arbuz.handlers.messages;

import com.vk.api.sdk.objects.groups.Group;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Класс обработчика команды /auth_as_admin
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@RequiredArgsConstructor
public final class AuthAsAdminHandler extends DjArbuzAbstractMessageHandler {
    /**
     * Поле хранилища групп, на которые оформлена подписка
     *
     * @see GroupBase
     */
    private final GroupBase groupsBase;
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
    private final AbstractVk vk;

    /**
     * Метод обрабатывающий команды /auth_as_admin
     *
     * @param message     сообщение пользователя
     * @param userReceivedRequestId id пользователя в системе бота
     * @return ответ
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userReceivedRequestId) {
        if (message != null) {
            return createIllegalArgumentMessage("/auth_as_admin", message)
                    .build(List.of(userReceivedRequestId));
        }

        if (!usersBase.contains(userReceivedRequestId)) {
            return NOT_AUTHED_USER.build(List.of(userReceivedRequestId));
        }

        BotUser userReceivedRequest = usersBase.getUser(userReceivedRequestId);
        List<? extends Group> userAdminGroups;
        try {
            userAdminGroups = vk.searchUserAdminGroups(userReceivedRequest);
        } catch (SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(List.of(userReceivedRequestId));
        }
        List<String> userAdminGroupsScreenName = userAdminGroups.stream().map(Group::getScreenName).toList();
        StringBuilder userAuthAsGroupAdminAnswer = new StringBuilder("Вы стали админом следующих групп:\n");
        for (String userAdminGroupScreenName : userAdminGroupsScreenName) {

            if (groupsBase.putIfAbsent(userAdminGroupScreenName)) {

                if (groupsBase.addAdmin(userAdminGroupScreenName, userReceivedRequestId)) {
                    userAuthAsGroupAdminAnswer.append(userAdminGroupScreenName).append(",\n");
                }

            }

        }
        userAuthAsGroupAdminAnswer.deleteCharAt(userAuthAsGroupAdminAnswer.length() - 2);
        return MessageHandlerResponse.newBuilder()
                .textMessage(userAuthAsGroupAdminAnswer.toString())
                .build(List.of(userReceivedRequestId));
    }
}
