package handlers.messages;

import bots.BotTextResponse;
import bots.StoppableByUser;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.SocialNetwork;
import database.GroupsStorage;
import database.UserStorage;
import socialnetworks.socialnetwork.groups.NoGroupException;
import socialnetworks.socialnetwork.groups.SubscribeStatus;
import user.User;
import user.CreateUser;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Класс для обработки сообщений пользователей, а также создающий ответы на них
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 2.2
 * @see MessageExecutable
 */
public class MessageExecutor implements MessageExecutable {
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
     * @see MessageExecutorResponse
     * @see BotTextResponse#AUTH_ERROR
     */
    private static final MessageExecutorResponse AUTH_ERROR = MessageExecutorResponse.newBuilder()
            .textMessage(BotTextResponse.AUTH_ERROR).build();
    /**
     * Поле help сообщения бота
     *
     * @see MessageExecutorResponse
     * @see BotTextResponse#HELP_INFO
     */
    private static final MessageExecutorResponse HELP_INFO = MessageExecutorResponse.newBuilder()
            .textMessage(BotTextResponse.HELP_INFO).build();
    /**
     * Поле сообщения о получении неизвестной команды
     *
     * @see MessageExecutorResponse
     * @see BotTextResponse#UNKNOWN_COMMAND
     */
    private static final MessageExecutorResponse UNKNOWN_COMMAND = MessageExecutorResponse.newBuilder()
            .textMessage(BotTextResponse.UNKNOWN_COMMAND).build();
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не аутентифицировался в социальной сети
     *
     * @see MessageExecutorResponse
     * @see BotTextResponse#NOT_AUTHED_USER
     */
    private static final MessageExecutorResponse NOT_AUTHED_USER = MessageExecutorResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_AUTHED_USER).build();
    /**
     * Поле сообщения с текстом, в котором говориться, что не нашлось постов в группе
     *
     * @see MessageExecutorResponse
     * @see BotTextResponse#NO_POSTS_IN_GROUP
     */
    private static final MessageExecutorResponse NO_POSTS_IN_GROUP = MessageExecutorResponse.newBuilder()
            .textMessage(BotTextResponse.NO_POSTS_IN_GROUP).build();
    /**
     * Поле хранилища групп, на которые оформлена подписка
     *
     * @see GroupsStorage
     */
    private final GroupsStorage groupsBase;
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserStorage
     */
    private final UserStorage usersBase;
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final SocialNetwork socialNetwork;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param groupsBase    хранилище групп на которые оформлены подписки
     * @param usersBase     хранилище пользователей, которые аутентифицированы в социальной сети
     * @param socialNetwork класс для взаимодействия с социальными сетями
     */
    public MessageExecutor(GroupsStorage groupsBase, UserStorage usersBase, SocialNetwork socialNetwork) {
        this.groupsBase = groupsBase;
        this.usersBase = usersBase;
        this.socialNetwork = socialNetwork;
    }

    /**
     * Метод определяющий команды в сообщении пользователя и возвращающий ответ
     *
     * @param message        сообщение пользователя
     * @param telegramUserId id пользователя в телеграмме
     * @param botThread      бот из которого был вызван метод
     * @return возвращает ответ на сообщение пользователя
     * @see MessageExecutorResponse
     * @see MessageExecutor#isItNoArgCommand(String[])
     * @see MessageExecutor#HELP_INFO
     * @see MessageExecutor#getAuthResponse()
     * @see MessageExecutor#getStopResponse(StoppableByUser)
     * @see UserStorage#contains(String)
     * @see MessageExecutor#NOT_AUTHED_USER
     * @see UserStorage#getUser(String)
     * @see MessageExecutor#isItSingleArgCommand(String[])
     * @see MessageExecutor#getGroupUrl(String, User)
     * @see MessageExecutor#getGroupId(String, User)
     * @see MessageExecutor#subscribeTo(String, User)
     * @see MessageExecutor#getFiveLastPosts(String, User)
     * @see MessageExecutor#UNKNOWN_COMMAND
     */
    @Override
    public MessageExecutorResponse executeMessage(String message, String telegramUserId, StoppableByUser botThread) {
        String[] commandAndArgs = message.split(" ", 2);

        if (isItNoArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/help" -> {
                    return HELP_INFO;
                }
                case "/auth" -> {
                    return getAuthResponse();
                }
                case "/stop" -> {
                    return getStopResponse(botThread);
                }
                default -> {
                    return UNKNOWN_COMMAND;
                }
            }
        }

        if (!usersBase.contains(telegramUserId)) {
            return NOT_AUTHED_USER;
        }

        User user = usersBase.getUser(telegramUserId);

        if (isItSingleArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/link" -> {
                    return getGroupUrl(commandAndArgs[ARG_INDEX], user);
                }
                case "/id" -> {
                    return getGroupId(commandAndArgs[ARG_INDEX], user);
                }
                case "/subscribe" -> {
                    return subscribeTo(commandAndArgs[ARG_INDEX], user);
                }
                case "/five_posts" -> {
                    return getFiveLastPosts(commandAndArgs[ARG_INDEX], user);
                }
            }
        }
        return UNKNOWN_COMMAND;
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
     * @return ответ на команду /auth
     * @see SocialNetwork#getAuthUrl()
     * @see SocialNetwork#createUser(String)
     * @see MessageExecutor#AUTH_ERROR
     * @see MessageExecutorResponse#newBuilder()
     * @see BotTextResponse#AUTH_GO_VIA_LINK
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#textMessage(String)
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#updateUser(CreateUser)
     */
    private MessageExecutorResponse getAuthResponse() {
        String authURL = socialNetwork.getAuthUrl();

        if (authURL == null) {
            return AUTH_ERROR;
        }

        return MessageExecutorResponse.newBuilder()
                .textMessage(BotTextResponse.AUTH_GO_VIA_LINK + authURL)
                .updateUser(socialNetwork)
                .build();
    }

    /**
     * Метод формирующий ответ на команду /stop
     *
     * @param botThread бот вызвавший метод
     * @return ответ на /stop содержит STOP_INFO
     * @see StoppableByUser#stopByUser()
     * @see BotTextResponse#STOP_INFO
     * @see MessageExecutorResponse#newBuilder()
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#textMessage(String)
     */
    private static MessageExecutorResponse getStopResponse(StoppableByUser botThread) {
        botThread.stopByUser();
        return MessageExecutorResponse.newBuilder()
                .textMessage(BotTextResponse.STOP_INFO)
                .build();
    }

    /**
     * Метод возвращающий ответ на /link
     *
     * @param groupName имя группы
     * @param user      пользователь отправивший сообщение
     * @return ссылку на верифицированную группу если такая нашлась
     * @see SocialNetwork#getGroupUrl(String, User)
     * @see MessageExecutorResponse#newBuilder()
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#textMessage(String)
     */
    private MessageExecutorResponse getGroupUrl(String groupName, User user) {
        try {
            return MessageExecutorResponse.newBuilder()
                    .textMessage(socialNetwork.getGroupUrl(groupName, user))
                    .build();
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageExecutorResponse.newBuilder().textMessage(e.getMessage()).build();
        }
    }

    /**
     * Метод возвращающий ответ на /id
     *
     * @param groupName имя группы
     * @param user      пользователь отправивший сообщение
     * @return id верифицированной группы если такая нашлась
     * @see SocialNetwork#getGroupId(String, User)
     * @see MessageExecutorResponse#newBuilder()
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#textMessage(String)
     */
    private MessageExecutorResponse getGroupId(String groupName, User user) {
        try {
            return MessageExecutorResponse.newBuilder()
                    .textMessage(socialNetwork.getGroupId(groupName, user))
                    .build();
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageExecutorResponse.newBuilder().textMessage(e.getMessage()).build();
        }
    }

    /**
     * Метод для подписки пользователя
     *
     * @param groupName Название группы
     * @param user      айди юзера
     * @return - возврат текста для сообщения
     * @see SocialNetwork#subscribeTo(GroupsStorage, String, User)
     * @see SubscribeStatus#getSubscribeMessage()
     * @see MessageExecutorResponse#newBuilder()
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#textMessage(String)
     */
    private MessageExecutorResponse subscribeTo(String groupName, User user) {
        try {
            return MessageExecutorResponse.newBuilder()
                    .textMessage(socialNetwork.subscribeTo(groupsBase, groupName, user).getSubscribeMessage())
                    .build();
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageExecutorResponse.newBuilder().textMessage(e.getMessage()).build();
        }
    }

    /**
     * Метод возвращающий ответ на ответ на /get_last_posts
     *
     * @param groupName имя группы
     * @param user      пользователь отправивший сообщение
     * @return текст постов, ссылки на изображения в них, а также ссылки
     * @see SocialNetwork#getLastPosts(String, int, User)
     * @see MessageExecutor#DEFAULT_POST_NUMBER
     * @see MessageExecutor#NO_POSTS_IN_GROUP
     * @see MessageExecutorResponse#newBuilder()
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#textMessage(String)
     * @see MessageExecutorResponse.MessageExecutorResponseBuilder#postsText(List)
     */
    private MessageExecutorResponse getFiveLastPosts(String groupName, User user) {
        try {
            return MessageExecutorResponse.newBuilder()
                    .postsText(socialNetwork.getLastPosts(groupName, DEFAULT_POST_NUMBER, user).orElseThrow())
                    .build();
        } catch (NoSuchElementException e) {
            return NO_POSTS_IN_GROUP;
        } catch (NoGroupException | SocialNetworkException e) {
            return MessageExecutorResponse.newBuilder().textMessage(e.getMessage()).build();
        }
    }
}