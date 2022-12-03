package dj.arbuz.handlers.messages;

import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.vk.VkConstants;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Класс для обработки команды /subscribed
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
@RequiredArgsConstructor
public class GetUserSubscribedGroupsLinks extends DjArbuzAbstractMessageHandler {
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
     * Метод возвращающий строку содержащую ссылки на группы, на которые подписан пользователь
     *
     * @param userSendResponseId id пользователю, которому будет отправлен ответ
     * @return ответ содержащий ссылки на группы, на которые подписан пользователь
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        if (message != null) {
            return createIllegalArgumentMessage("/subscribed", message).build(List.of(userSendResponseId));
        }

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
            userSubscribedGroupsLinks.append(counter)
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
