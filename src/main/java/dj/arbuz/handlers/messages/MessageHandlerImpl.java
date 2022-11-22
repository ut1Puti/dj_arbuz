package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.bots.StoppableByUser;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.AbstractSocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.database.local.GroupsStorage;
import dj.arbuz.database.local.UserStorage;
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
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder AUTH_ERROR = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.AUTH_ERROR);
    /**
     * Поле help сообщения бота
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#HELP_INFO
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder HELP_INFO = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.HELP_INFO);
    /**
     * Поле сообщения о получении неизвестной команды
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#UNKNOWN_COMMAND
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder UNKNOWN_COMMAND = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.UNKNOWN_COMMAND);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не аутентифицировался в социальной сети
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_AUTHED_USER
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder NOT_AUTHED_USER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_AUTHED_USER);
    /**
     * Поле сообщения с текстом, в котором говориться, что не нашлось постов в группе
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_POSTS_IN_GROUP
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder NO_POSTS_IN_GROUP = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_POSTS_IN_GROUP);
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
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не подписан ни на одну группу
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_SUBSCRIBED_GROUPS
     */
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder NO_SUBSCRIBED_GROUPS = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_SUBSCRIBED_GROUPS);
    /**
     * Поле хранилища групп, на которые оформлена подписка
     *
     * @see GroupsStorage
     */
    private final GroupBase groupsBase;
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserStorage
     */
    private final UserBase usersBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final Vk socialNetwork;

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
        this.socialNetwork = vk;
    }

    /**
     * Метод определяющий команды в сообщении пользователя и возвращающий ответ
     *
     * @param message               сообщение пользователя
     * @param userSendResponseId    id пользователя в телеграмме
     * @param stoppableByUserThread бот из которого был вызван метод
     * @return возвращает ответ на сообщение пользователя
     * @see MessageHandlerResponse
     * @see MessageHandlerImpl#isItNoArgCommand(String[])
     * @see MessageHandlerImpl#HELP_INFO
     * @see MessageHandlerImpl#getAuthResponse(String)
     * @see MessageHandlerImpl#getStopResponse(StoppableByUser, String)
     * @see UserStorage#contains(String)
     * @see MessageHandlerImpl#NOT_AUTHED_USER
     * @see UserStorage#getUser(String)
     * @see MessageHandlerImpl#isItSingleArgCommand(String[])
     * @see MessageHandlerImpl#getGroupUrl(String, String)
     * @see MessageHandlerImpl#getGroupId(String, String)
     * @see MessageHandlerImpl#subscribeTo(String, String)
     * @see MessageHandlerImpl#getFiveLastPosts(String, String)
     * @see MessageHandlerImpl#UNKNOWN_COMMAND
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId, StoppableByUser stoppableByUserThread) {
        String[] commandAndArgs = message.split(" ", 2);

        if (isItNoArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/help" -> {
                    return HELP_INFO.build(userSendResponseId);
                }
                case "/auth" -> {
                    return getAuthResponse(userSendResponseId);
                }
                case "/stop" -> {
                    return getStopResponse(stoppableByUserThread, userSendResponseId);
                }
                case "/subscribed" -> {
                    return getUserSubscribedGroupsLinks(userSendResponseId);
                }
                default -> {
                    return UNKNOWN_COMMAND.build(userSendResponseId);
                }
            }
        }

        if (isItSingleArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/link" -> {
                    return getGroupUrl(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/id" -> {
                    return getGroupId(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/subscribe" -> {
                    return subscribeTo(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/unsubscribe" -> {
                    return unsubscribeFrom(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
                case "/five_posts" -> {
                    return getFiveLastPosts(commandAndArgs[ARG_INDEX], userSendResponseId);
                }
            }
        }
        return UNKNOWN_COMMAND.build(userSendResponseId);
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
     * @see AbstractSocialNetwork#createBotUserAsync(String)
     * @see MessageHandlerImpl#AUTH_ERROR
     * @see MessageHandlerResponse#newBuilder()
     * @see BotTextResponse#AUTH_GO_VIA_LINK
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#updateUser(CompletableFuture)
     */
    private MessageHandlerResponse getAuthResponse(String userSendResponseId) {
        String authURL = socialNetwork.getAuthUrl(userSendResponseId);

        if (authURL == null) {
            return AUTH_ERROR.build(userSendResponseId);
        }

        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.AUTH_GO_VIA_LINK + authURL)
                .updateUser(socialNetwork.createBotUserAsync(userSendResponseId))
                .build(userSendResponseId);
    }

    /**
     * Метод формирующий ответ на команду /stop
     *
     * @param stoppableByUserThread бот вызвавший метод
     * @param userSendResponseId    id пользователю, которому будет отправлен ответ
     * @return ответ на /stop содержит STOP_INFO
     * @see StoppableByUser#stopByUser()
     * @see BotTextResponse#STOP_INFO
     * @see MessageHandlerResponse#newBuilder()
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     */
    private static MessageHandlerResponse getStopResponse(StoppableByUser stoppableByUserThread, String userSendResponseId) {
        stoppableByUserThread.stopByUser();
        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.STOP_INFO)
                .build(userSendResponseId);
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
            return NOT_AUTHED_USER.build(userSendResponseId);
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(socialNetwork.getGroupUrl(userReceivedGroupName, userCallingMethod))
                    .build(userSendResponseId);
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(userSendResponseId);
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
    private MessageHandlerResponse getGroupId(String userReceivedGroupName, String userSendResponseId) {

        if (!usersBase.contains(userSendResponseId)) {
            return NOT_AUTHED_USER.build(userSendResponseId);
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(socialNetwork.getGroupId(userReceivedGroupName, userCallingMethod))
                    .build(userSendResponseId);
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(userSendResponseId);
        }
    }

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
            return NOT_AUTHED_USER.build(userSendResponseId);
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(socialNetwork.subscribeTo(groupsBase, userReceivedGroupName, userCallingMethod).getSubscribeMessage())
                    .build(userSendResponseId);
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(userSendResponseId);
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
            return NOT_AUTHED_USER.build(userSendResponseId);
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        try {
            boolean isUnsubscribed = socialNetwork.unsubscribeFrom(groupsBase, userReceivedGroupName, userCallingMethod);

            if (isUnsubscribed) {
                return UNSUBSCRIBED.build(userSendResponseId);
            }

            return NOT_SUBSCRIBER.build(userSendResponseId);
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(userSendResponseId);
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
            return NOT_AUTHED_USER.build(userSendResponseId);
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);
        Set<String> userSubscribedGroupsName = groupsBase.getUserSubscribedGroups(userCallingMethod.getTelegramId());

        if (userSubscribedGroupsName.isEmpty()) {
            return NO_SUBSCRIBED_GROUPS.build(userSendResponseId);
        }

        StringBuilder userSubscribedGroupsLinks = new StringBuilder();
        for (String userSubscribedGroupName : userSubscribedGroupsName) {
            userSubscribedGroupsLinks.append(VkConstants.VK_ADDRESS).append(userSubscribedGroupName).append("\n");
        }
        return MessageHandlerResponse.newBuilder()
                .textMessage(userSubscribedGroupsLinks.toString())
                .build(userSendResponseId);
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
            return NOT_AUTHED_USER.build(userSendResponseId);
        }

        BotUser userCallingMethod = usersBase.getUser(userSendResponseId);

        List<String> groupFindPosts;
        try {
            groupFindPosts = socialNetwork.getLastPostsAsStrings(userReceivedGroupName, DEFAULT_POST_NUMBER, userCallingMethod);
        } catch (NoSuchElementException e) {
            return NO_POSTS_IN_GROUP.build(userSendResponseId);
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageHandlerResponse.newBuilder()
                    .textMessage(e.getMessage())
                    .build(userSendResponseId);
        }

        if (groupFindPosts.isEmpty()) {
            return NO_POSTS_IN_GROUP.build(userSendResponseId);
        }

        return MessageHandlerResponse.newBuilder()
                .postsText(groupFindPosts)
                .build(userSendResponseId);
    }
}
