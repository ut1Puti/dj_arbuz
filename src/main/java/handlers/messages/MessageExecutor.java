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
 * Класс утилитных методов создающий ответы на сообщения пользователя
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 2.0
 */
public class MessageExecutor {
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
     * @param groupsBase хранилище групп на которые оформлены подписки
     * @param usersBase хранилище пользователей, которые аутентифицированы в социальной сети
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
     * @param message   сообщение пользователя
     * @param telegramUserId id пользователя в телеграмме
     * @param botThread бот из которого был вызван метод
     * @return возвращает ответ на сообщение пользователя
     * @see MessageExecutorResponse
     * @see MessageExecutor#isItNoArgCommand(String[])
     * @see MessageExecutor#getHelpResponse()
     * @see MessageExecutor#getAuthResponse()
     * @see MessageExecutor#getStopResponse(StoppableByUser)
     * @see MessageExecutor#getUnknownCommandResponse()
     * @see UserStorage#contains(String)
     * @see UserStorage#getUser(String)
     * @see MessageExecutor#isItSingleArgCommand(String[])
     * @see MessageExecutor#getGroupURL(String, User)
     * @see MessageExecutor#getGroupId(String, User)
     * @see MessageExecutor#subscribeTo(String, User)
     * @see MessageExecutor#getFiveLastPosts(String, User)
     */
    public MessageExecutorResponse executeMessage(String message, String telegramUserId, StoppableByUser botThread) {
        String[] commandAndArgs = message.split(" ", 2);

        if (isItNoArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/help" -> {
                    return getHelpResponse();
                }
                case "/auth" -> {
                    return getAuthResponse();
                }
                case "/stop" -> {
                    return getStopResponse(botThread);
                }
                default -> {
                    return getUnknownCommandResponse();
                }
            }
        }

        if (!usersBase.contains(telegramUserId)) {
            return getNotAuthedResponse();
        }

        User user = usersBase.getUser(telegramUserId);

        if (isItSingleArgCommand(commandAndArgs)) {
            switch (commandAndArgs[COMMAND_INDEX]) {
                case "/link" -> {
                    return getGroupURL(commandAndArgs[ARG_INDEX], user);
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
        return getUnknownCommandResponse();
    }

    /**
     * Метод проверяет есть ли аргументы в полученной команде
     *
     * @param commandAndArgs - массив аргументов и комманд
     * @return true - если нет аргументов, false - если есть аргументы
     */
    private static boolean isItNoArgCommand(String[] commandAndArgs) {
        return commandAndArgs.length == 1;
    }

    /**
     * Метод проверяет есть ли аргументы в полученной команде
     *
     * @param commandAndArgs - массив аргументов и комманд
     * @return true - если есть только один аргумент, false - если нет аргументов или их больше одного
     */
    private static boolean isItSingleArgCommand(String[] commandAndArgs) {
        return commandAndArgs.length == 2;
    }

    /**
     * Метод формирующий ответ на команду /help
     *
     * @return ответ на команду /help содержит HELP_INFO
     * @see BotTextResponse#HELP_INFO
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private static MessageExecutorResponse getHelpResponse() {
        return new MessageExecutorResponse(BotTextResponse.HELP_INFO);
    }

    /**
     * Метод формирующий ответ на команду /auth
     *
     * @return ответ на команду /auth
     * @see SocialNetwork#getAuthUrl()
     * @see SocialNetwork#createUser(String)
     * @see BotTextResponse#AUTH_ERROR
     * @see BotTextResponse#AUTH_GO_VIA_LINK
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     * @see MessageExecutorResponse#MessageExecutorResponse(String, CreateUser)
     */
    private MessageExecutorResponse getAuthResponse() {
        String authURL = socialNetwork.getAuthUrl();

        if (authURL == null) {
            return new MessageExecutorResponse(BotTextResponse.AUTH_ERROR);
        }

        return new MessageExecutorResponse(BotTextResponse.AUTH_GO_VIA_LINK + authURL, socialNetwork);
    }

    /**
     * Метод формирующий ответ на команду /stop
     *
     * @param botThread - бот вызвавший метод
     * @return ответ на /stop содержит STOP_INFO
     * @see StoppableByUser#stopByUser()
     * @see BotTextResponse#STOP_INFO
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private static MessageExecutorResponse getStopResponse(StoppableByUser botThread) {
        botThread.stopByUser();
        return new MessageExecutorResponse(BotTextResponse.STOP_INFO);
    }

    /**
     * Метод формирующий ответ если пользователь обращается к ф-циям требующим аутентификации
     *
     * @return ответ содержащий NOT_AUTHED_USER
     * @see BotTextResponse#NOT_AUTHED_USER
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private static MessageExecutorResponse getNotAuthedResponse() {
        return new MessageExecutorResponse(BotTextResponse.NOT_AUTHED_USER);
    }

    /**
     * Метод возвращающий ответ на /link
     *
     * @param groupName - имя группы
     * @param user      - пользователь отправивший сообщение
     * @return ссылку на верифицированную группу если такая нашлась
     * @see SocialNetwork#getGroupUrl(String, User)
     * @see BotTextResponse#UPDATE_TOKEN
     * @see BotTextResponse#VK_API_ERROR
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private MessageExecutorResponse getGroupURL(String groupName, User user) {
        try {
            return new MessageExecutorResponse(socialNetwork.getGroupUrl(groupName, user));
        } catch (NoGroupException | SocialNetworkException e) {
            return new MessageExecutorResponse(e.getMessage());
        }
    }

    /**
     * Метод возвращающий ответ на /id
     *
     * @param groupName - имя группы
     * @param user      - пользователь отправивший сообщение
     * @return id верифицированной группы если такая нашлась
     * @see SocialNetwork#getGroupId(String, User)
     * @see BotTextResponse#UPDATE_TOKEN
     * @see BotTextResponse#VK_API_ERROR
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private MessageExecutorResponse getGroupId(String groupName, User user) {
        try {
            return new MessageExecutorResponse(socialNetwork.getGroupId(groupName, user));
        } catch (NoGroupException | SocialNetworkException e) {
            return new MessageExecutorResponse(e.getMessage());
        }
    }

    /**
     * Метод для подписки пользователя
     *
     * @param groupName - Название группы
     * @param user      - айди юзера
     * @return - возврат текста для сообщения
     * @see SocialNetwork#subscribeTo(GroupsStorage, String, User)
     * @see SubscribeStatus#getSubscribeMessage()
     * @see BotTextResponse#UPDATE_TOKEN
     * @see BotTextResponse#VK_API_ERROR
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private MessageExecutorResponse subscribeTo(String groupName, User user) {
        try {
            return new MessageExecutorResponse(socialNetwork.subscribeTo(groupsBase, groupName, user).getSubscribeMessage());
        } catch (NoGroupException | SocialNetworkException e) {
            return new MessageExecutorResponse(e.getMessage());
        }
    }

    /**
     * Метод возвращающий ответ на ответ на /get_last_posts
     *
     * @param groupName - имя группы
     * @param user      - пользователь отправивший сообщение
     * @return текст постов, ссылки на изображения в них, а также ссылки
     * @see SocialNetwork#getLastPosts(String, int, User)
     * @see MessageExecutor#DEFAULT_POST_NUMBER
     * @see BotTextResponse#UPDATE_TOKEN
     * @see BotTextResponse#NO_POSTS_IN_GROUP
     * @see BotTextResponse#VK_API_ERROR
     * @see MessageExecutorResponse#MessageExecutorResponse(List)
     */
    private MessageExecutorResponse getFiveLastPosts(String groupName, User user) {
        try {
            return new MessageExecutorResponse(socialNetwork.getLastPosts(groupName, DEFAULT_POST_NUMBER, user).orElseThrow());
        } catch (NoSuchElementException e) {
            return new MessageExecutorResponse(BotTextResponse.NO_POSTS_IN_GROUP);
        } catch (NoGroupException | SocialNetworkException | IllegalArgumentException e) {
            return new MessageExecutorResponse(e.getMessage());
        }
    }

    /**
     * Метод возвращающий ответ при получении неизвестной команды
     *
     * @return ответ содержащий UNKNOWN_COMMAND
     * @see BotTextResponse#UNKNOWN_COMMAND
     * @see MessageExecutorResponse#MessageExecutorResponse(String)
     */
    private static MessageExecutorResponse getUnknownCommandResponse() {
        return new MessageExecutorResponse(BotTextResponse.UNKNOWN_COMMAND);
    }
}