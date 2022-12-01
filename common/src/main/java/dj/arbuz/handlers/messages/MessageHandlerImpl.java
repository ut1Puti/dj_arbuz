package dj.arbuz.handlers.messages;

import com.vk.api.sdk.objects.groups.Group;
import dj.arbuz.BotTextResponse;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;
import dj.arbuz.socialnetworks.socialnetwork.AbstractSocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.socialnetwork.groups.SubscribeStatus;
import dj.arbuz.socialnetworks.vk.Vk;
import dj.arbuz.socialnetworks.vk.VkConstants;
import dj.arbuz.user.BotUser;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Класс для обработки сообщений пользователей, а также создающий ответы на них
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 2.2
 * @see MessageHandler
 */
public final class MessageHandlerImpl implements MessageHandler {
    /**
     * Поле кол-ва запрашиваемых последних постов
     */
    private static final int DEFAULT_POST_NUMBER = 5;
    /**
     * Поле индекса команды
     */
    private static final int COMMAND_INDEX = 0;
    /**
     * Поле индекса аргумента
     */
    private static final int ARG_INDEX = 1;
    /**
     * Поле сообщения при ошибке аутентификации пользователя
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#AUTH_ERROR
     */
    private static final MessageHandlerResponseBuilder AUTH_ERROR = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.AUTH_ERROR);
    /**
     * Поле help сообщения бота
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#HELP_INFO
     */
    private static final MessageHandlerResponseBuilder HELP_INFO = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.HELP_INFO);
    /**
     * Поле сообщения о получении неизвестной команды
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#UNKNOWN_COMMAND
     */
    private static final MessageHandlerResponseBuilder UNKNOWN_COMMAND = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.UNKNOWN_COMMAND);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не аутентифицировался в социальной сети
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_AUTHED_USER
     */
    private static final MessageHandlerResponseBuilder NOT_AUTHED_USER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_AUTHED_USER);
    /**
     * Поле сообщения с текстом, в котором говориться, что не нашлось постов в группе
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_POSTS_IN_GROUP
     */
    private static final MessageHandlerResponseBuilder NO_POSTS_IN_GROUP = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_POSTS_IN_GROUP);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь быд отписан от группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#UNSUBSCRIBED
     */
    private static final MessageHandlerResponseBuilder UNSUBSCRIBED = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.UNSUBSCRIBED);
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
     *
     */
    private static final MessageHandlerResponseBuilder IS_NOT_ADMIN = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.IS_NOT_ADMIN);
    /**
     * Поле хранилища групп, на которые оформлена подписка
     *
//     * @see GroupsStorage
     */
    private final GroupBase groupsBase;
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
//     * @see UserStorage
     */
    private final UserBase usersBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final Vk vk;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param groupsBase хранилище групп на которые оформлены подписки
     * @param usersBase  хранилище пользователей, которые аутентифицированы в социальной сети
     * @param vk         класс для взаимодействия с vk
     */
    public MessageHandlerImpl(GroupBase groupsBase, UserBase usersBase, Vk vk) {
        this.groupsBase = groupsBase;
        this.usersBase = usersBase;
        this.vk = vk;
    }

    /**
     * Метод определяющий команды в сообщении пользователя и возвращающий ответ
     *
     * @param message               сообщение пользователя
     * @param userSendResponseId    id пользователя в телеграмме
     * @return возвращает ответ на сообщение пользователя
     * @see MessageHandlerResponse
     * @see MessageHandlerImpl#isItNoArgCommand(String[])
     * @see MessageHandlerImpl#HELP_INFO
     * @see MessageHandlerImpl#getAuthResponse(String)
//     * @see UserStorage#contains(String)
     * @see MessageHandlerImpl#NOT_AUTHED_USER
//     * @see UserStorage#getUser(String)
     * @see MessageHandlerImpl#isItSingleArgCommand(String[])
     * @see MessageHandlerImpl#getGroupUrl(String, String)
//     * @see MessageHandlerImpl#getGroupId(String, String)
     * @see MessageHandlerImpl#subscribeTo(String, String)
     * @see MessageHandlerImpl#getFiveLastPosts(String, String)
     * @see MessageHandlerImpl#UNKNOWN_COMMAND
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        String[] commandAndArgs = message.split(" ", 2);

        if (isItNoArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/help" -> {
                    MessageTelegramHandler getHelp = new HelpInfo();
                    return getHelp.sendMessage(null,userSendResponseId);
                }
                case "/auth" -> {
                    return getAuthResponse(userSendResponseId);
                }
                case "/auth_as_admin" -> {
                    return authAsAdmin(userSendResponseId);
                }
                case "/stop" -> {
                    return getStopResponse(userSendResponseId);
                }
                case "/subscribed" -> {
                    return getUserSubscribedGroupsLinks(userSendResponseId);
                }
                default -> {
                    return UNKNOWN_COMMAND.build(List.of(userSendResponseId));
                }
            }
        }

        if (isItSingleArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/post_to_all" -> {
                    return postToAllUsers(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/post_to_group" -> {
                    return postToSubscribers(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/link" -> {
                    MessageTelegramHandler getGroupUrlMessage = new GetGroupUrl(usersBase, vk);
                    return  getGroupUrlMessage.sendMessage(commandAndArgs[ARG_INDEX], userSendResponseId);
//                    return getGroupUrl(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/id" -> {
                    MessageTelegramHandler groupIdMessage = new GetGroupId(usersBase, vk);
                    return groupIdMessage.sendMessage(commandAndArgs[ARG_INDEX],userSendResponseId);
//                    return getGroupId(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/subscribe" -> {
                    MessageTelegramHandler subscribeToMessage = new SubsribeTo(groupsBase,usersBase, vk);
                    return subscribeToMessage.sendMessage(commandAndArgs[ARG_INDEX], userSendResponseId);
//                    return subscribeTo(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/unsubscribe" -> {
                    MessageTelegramHandler unsubscribeFrom = new UnsubscribeFrom(groupsBase,usersBase, vk);
                    return unsubscribeFrom.sendMessage(commandAndArgs[ARG_INDEX], userSendResponseId);
//                    return unsubscribeFrom(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/five_posts" -> {
                    MessageTelegramHandler getFiveLastPosts = new GetFiveLastPosts(usersBase, vk);
                    return getFiveLastPosts.sendMessage(commandAndArgs[ARG_INDEX], userSendResponseId);
//                    return getFiveLastPosts(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
            }
        }
        return UNKNOWN_COMMAND.build(List.of(userSendResponseId));
    }

    /**
     * Метод проверяет есть ли аргументы в полученной команде
     *
     * @param commandAndArgs - массив аргументов и команд
     * @return {@code true} - если нет аргументов, {@code false} - если есть аргументы
     */
    private static boolean isItNoArgCommand(String[] commandAndArgs) {
        return commandAndArgs.length == 1;
    }

    /**
     * Метод проверяет есть ли аргументы в полученной команде
     *
     * @param commandAndArgs - массив аргументов и команд
     * @return {@code true} - если есть только один аргумент, {@code false} - если нет аргументов или их больше одного
     */
    private static boolean isItSingleArgCommand(String[] commandAndArgs) {
        return commandAndArgs.length == 2;
    }

    /**
     * Метод формирующий ответ на команду /auth
     *
     * @param userSendResponseId id пользователю, которому будет отправлен ответ
     * @return ответ на команду /auth
     * @see AbstractSocialNetwork#getAuthUrl(String)
     * @see AbstractSocialNetwork#createBotUser(String)
     * @see MessageHandlerImpl#AUTH_ERROR
     * @see MessageHandlerResponse#newBuilder()
     * @see BotTextResponse#AUTH_GO_VIA_LINK
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#additionalMesage(CompletableFuture) 
     */
    private MessageHandlerResponse getAuthResponse(String userSendResponseId) {
        String authURL = vk.getAuthUrl(userSendResponseId);

        if (authURL == null) {
            return AUTH_ERROR.build(List.of(userSendResponseId));
        }

        CompletableFuture<String> createUserActorAnswer =
                CompletableFuture.supplyAsync(() -> botUserAuth(vk.createBotUser(userSendResponseId)));

        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.AUTH_GO_VIA_LINK + authURL)
                .additionalMesage(createUserActorAnswer)
                .build(List.of(userSendResponseId));
    }

    private String botUserAuth(BotUser botUserCreated) {
        if (botUserCreated == null) {
            return BotTextResponse.AUTH_ERROR;
        } else if (usersBase.addUser(botUserCreated.getTelegramId(), botUserCreated)) {
            return BotTextResponse.AUTH_SUCCESS;
        } else {
            return BotTextResponse.AUTH_ERROR;
        }
    }

    private MessageHandlerResponse authAsAdmin(String userReceivedRequestId) {

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
                userAuthAsGroupAdminAnswer.append(userAdminGroupScreenName).append(",\n");
            }

        }
        userAuthAsGroupAdminAnswer.deleteCharAt(userAuthAsGroupAdminAnswer.length() - 2);
        return MessageHandlerResponse.newBuilder()
                .textMessage(userAuthAsGroupAdminAnswer.toString())
                .build(List.of(userReceivedRequestId));
    }

    /**
     * Метод формирующий ответ на команду /stop
     *
     * @param userReceivedMessageId    id пользователю, которому будет отправлен ответ
     * @return ответ на /stop содержит STOP_INFO
     * @see BotTextResponse#STOP_INFO
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    private MessageHandlerResponse getStopResponse(String userReceivedMessageId) {
        if (usersBase.isAdmin(userReceivedMessageId)) {
            System.exit(0);
        }

        usersBase.deleteUser(userReceivedMessageId);
        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.STOP_INFO)
                .build(List.of(userReceivedMessageId));
    }

    /**
     * Метод отправляющий сообщение всем пользователям
     *
     * @param usersSendMessage сообщение, которое будет отправлено пользователям
     * @param userReceivedMessageId id пользователя от имени которого будет отправлено сообщение
     * @return ответ содержащий сообщение верифицированного пользователя, от имени которого будет отправлено сообщение
     */
    private MessageHandlerResponse postToAllUsers(String usersSendMessage, String userReceivedMessageId) {
        if (!usersBase.isAdmin(userReceivedMessageId)) {
            return UNKNOWN_COMMAND.build(List.of(userReceivedMessageId));
        }

        return MessageHandlerResponse.newBuilder().textMessage(usersSendMessage).build(usersBase.getAllUsersId());
    }

    /**
     *
     *
     * @param usersSendMessage
     * @param userReceivedMessageId
     * @return
     */
    private MessageHandlerResponse postToSubscribers(String usersSendMessage, String userReceivedMessageId) {
        String[] groupNameAndPostText = usersSendMessage.split(" ", 2);

        if (!groupsBase.isGroupAdmin(groupNameAndPostText[0], userReceivedMessageId)) {
            return IS_NOT_ADMIN.build(List.of(userReceivedMessageId));
        }

        if (!groupsBase.containsGroup(groupNameAndPostText[0])) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(new NoGroupException(groupNameAndPostText[0]).getMessage())
                    .build(List.of(userReceivedMessageId));
        }

        return MessageHandlerResponse.newBuilder()
                .textMessage(groupNameAndPostText[1])
                .build(groupsBase.getSubscribedToGroupUsersId(groupNameAndPostText[1]));
    }

    /**
     * Метод возвращающий ответ на /link
     *
     * @param userReceivedGroupName имя группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return ссылку на верифицированную группу если такая нашлась
     * @see AbstractSocialNetwork#getGroupUrl(String, Object)
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    private MessageHandlerResponse getGroupUrl(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(vk.getGroupUrl(userReceivedGroupName, userCallingMethod))
                    .build(List.of(userSendResponseId));
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(List.of(userSendResponseId));
        }
    }

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
//    private MessageHandlerResponse getGroupId(String userReceivedGroupName, String userSendResponseId) {
//
//        if (!usersBase.contains(userSendResponseId)) {
//            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
//        }
//
//        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);
//
//        try {
//            return MessageHandlerResponse.newBuilder()
//                    .textMessage(socialNetwork.getGroupId(userReceivedGroupName, userCallingMethod))
//                    .build(List.of(userSendResponseId));
//        } catch (NoGroupException | SocialNetworkException e) {
//            return MessageHandlerResponse.newBuilder()
//                    .textMessage(e.getMessage())
//                    .build(List.of(userSendResponseId));
//        }
//    }

    /**
     * Метод для подписки пользователя
     *
     * @param userReceivedGroupName Название группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return возвращает ответ содержащий информацию о статусе подписки пользователя
     * @see AbstractSocialNetwork#subscribeTo(GroupBase, String, Object)
     * @see SubscribeStatus#getSubscribeMessage()
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    private MessageHandlerResponse subscribeTo(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(vk.subscribeTo(groupsBase, userReceivedGroupName, userCallingMethod).getSubscribeMessage())
                    .build(List.of(userSendResponseId));
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(List.of(userSendResponseId));
        }
    }

    /**
     * Метод для отписывания пользователей от группы
     *
     * @param userReceivedGroupName название группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return ответ с сообщением о статусе отписки пользователя
     * @see AbstractSocialNetwork#unsubscribeFrom(GroupBase, String, Object)
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    private MessageHandlerResponse unsubscribeFrom(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            boolean isUnsubscribed = vk.unsubscribeFrom(groupsBase, userReceivedGroupName, userCallingMethod);

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

    /**
     * Метод возвращающий строку содержащую ссылки на группы, на которые подписан пользователь
     *
     * @param userSendResponseId id пользователю, которому будет отправлен ответ
     * @return ответ содержащий ссылки на группы, на которые подписан пользователь
     */
    private MessageHandlerResponse getUserSubscribedGroupsLinks(String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);
        Set<String> userSubscribedGroupsName = groupsBase.getUserSubscribedGroups(userCallingMethod.getTelegramId());

        if (userSubscribedGroupsName.isEmpty()) {
            return NO_SUBSCRIBED_GROUPS.build(List.of(userSendResponseId));
        }

        StringBuilder userSubscribedGroupsLinks = new StringBuilder();
        for (String userSubscribedGroupName : userSubscribedGroupsName) {
            userSubscribedGroupsLinks.append(VkConstants.VK_ADDRESS).append(userSubscribedGroupName).append("\n");
        }
        return MessageHandlerResponse.newBuilder()
                .textMessage(userSubscribedGroupsLinks.toString())
                .build(List.of(userSendResponseId));
    }

    /**
     * Метод возвращающий ответ на ответ на /get_last_posts
     *
     * @param userReceivedGroupName имя группы
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return текст постов, ссылки на изображения в них, а также ссылки
     * @see AbstractSocialNetwork#getLastPostsAsStrings(String, int, Object)
     * @see MessageHandlerImpl#DEFAULT_POST_NUMBER
     * @see MessageHandlerImpl#NO_POSTS_IN_GROUP
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#postsText(List)
     */
    private MessageHandlerResponse getFiveLastPosts(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(List.of(userSendResponseId));
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        List<String> groupFindPosts;
        try {
            groupFindPosts = vk.getLastPostsAsStrings(userReceivedGroupName, DEFAULT_POST_NUMBER, userCallingMethod);
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
