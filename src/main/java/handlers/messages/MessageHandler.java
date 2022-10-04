package handlers.messages;

import bots.ConsoleBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ApiTokenExtensionRequiredException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.vkapi.NoGroupException;
import handlers.vkapi.VkApiHandler;
import user.User;

import java.util.Objects;

/**
 * Класс утилитных методов создающий ответы на сообщения пользователя
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 1.0
 */
public class MessageHandler {
    /**
     * Поле обработчика запросов к Vk API
     */
    private static VkApiHandler vk = new VkApiHandler("src/main/resources/anonsrc/vkconfig.properties");

    /**
     * Метод определяющий команды в сообщении пользователя и возвращающий ответ
     *
     * @param message    - сообщение пользователя
     * @param user       - пользователь отправивший сообщение
     * @param callingBot - бот из которого был вызван метод
     * @return возвращает ответ на сообщение пользователя
     */
    public static HandlerResponse executeMessage(String message, User user, ConsoleBot callingBot) {
        String[] commandAndArg = message.split(" ", 2);
        if (commandAndArg.length == 1) {
            switch (commandAndArg[0]) {
                case "/help" -> {
                    return getHelpResponse();
                }
                case "/start", "/relogin" -> {
                    return getStartReloginResponse();
                }
                case "/stop" -> {
                    return getStopResponse(callingBot);
                }
            }
        }

        if (user == null) {
            return getNotAuthedResponse();
        }

        if (commandAndArg.length == 2) {
            switch (commandAndArg[0]) {
                case "/link" -> {
                    return getGroupURL(commandAndArg[1], user);
                }
                case "/id" -> {
                    return getGroupId(commandAndArg[1], user);
                }
                case "/subscribe" -> {
                    return subscribeTo(commandAndArg[1], user);
                }
                case "/get_last_posts" -> {
                    return getLastPosts(commandAndArg[1], user);
                }
            }
        }
        return getUnknownCommandResponse();
    }

    /**
     * Метод формирующий ответ на команду /help
     *
     * @return ответ на команду /help содержит HELP_INFO
     */
    private static HandlerResponse getHelpResponse() {
        return new HandlerResponse(TextResponse.HELP_INFO);
    }

    /**
     * Метод формирующий ответ на команды /start, /relogin
     *
     * @return ответ на команду /start, /relogin
     */
    private static HandlerResponse getStartReloginResponse() {
        String authURL = vk.getAuthURL();

        if (authURL == null) {
            return new HandlerResponse(TextResponse.AUTH_ERROR);
        }

        return new HandlerResponse(TextResponse.AUTH_GO_VIA_LINK + authURL + ".", vk);
    }

    /**
     * Метод формирующий ответ на команду /stop
     *
     * @param callingBot - бот вызвавший метод
     * @return ответ на /stop содержит STOP_INFO
     */
    private static HandlerResponse getStopResponse(ConsoleBot callingBot) {
        callingBot.stop();
        return new HandlerResponse(TextResponse.STOP_INFO);
    }

    /**
     * Метод формирующий ответ если пользователь обращается к ф-циям требующим аутентификации
     *
     * @return ответ содержащий NOT_AUTHED_USER
     */
    private static HandlerResponse getNotAuthedResponse() {
        return new HandlerResponse(TextResponse.NOT_AUTHED_USER);
    }

    /**
     * Метод возвращающий ответ на /link
     *
     * @param groupName - имя группы
     * @param user      - пользователь отправивший сообщение
     * @return ссылку на верефицированную группу если такая нашлась
     */
    private static HandlerResponse getGroupURL(String groupName, User user) {
        try {
            return new HandlerResponse(vk.getGroupURL(groupName, user));
        } catch (ApiTokenExtensionRequiredException e) {
            return new HandlerResponse(TextResponse.UPDATE_TOKEN);
        } catch (NoGroupException e) {
            return new HandlerResponse(TextResponse.NO_GROUP);
        } catch (ApiException | ClientException e) {
            return new HandlerResponse(TextResponse.VK_API_ERROR);
        }
    }

    /**
     * Метод возвращающий ответ на /id
     *
     * @param groupName - имя группы
     * @param user      - пользователь отправивший сообщение
     * @return id верефицированной группы если такая нашлась
     */
    private static HandlerResponse getGroupId(String groupName, User user) {
        try {
            return new HandlerResponse(String.valueOf(vk.getGroupId(groupName, user)));
        } catch (ApiTokenExtensionRequiredException e) {
            return new HandlerResponse(TextResponse.UPDATE_TOKEN);
        } catch (NoGroupException e) {
            return new HandlerResponse(TextResponse.NO_GROUP);
        } catch (ApiException | ClientException e) {
            return new HandlerResponse(TextResponse.VK_API_ERROR);
        }
    }

    /**
     * Метод для подписки пользователя
     *
     * @param groupName - Название группы
     * @param user - айди юзера
     * @return - возврат текста для сообщения
     */
    private static HandlerResponse subscribeTo(String groupName, User user) {
        try {
            return new HandlerResponse(vk.subscribeTo(groupName, user) ?
                    TextResponse.SUBSCRIBE : TextResponse.ALREADY_SUBSCRIBER);
        } catch (ApiTokenExtensionRequiredException e) {
            return new HandlerResponse(TextResponse.UPDATE_TOKEN);
        } catch (NoGroupException e) {
            return new HandlerResponse(TextResponse.NO_GROUP);
        } catch (ApiException | ClientException e) {
            return new HandlerResponse(TextResponse.VK_API_ERROR);
        }
    }

    /**
     * Метод возвращающий ответ на ответ на /get_last_posts
     *
     * @param groupName - имя группы
     * @param user      - пользователь отправивший сообщение
     * @return текст постов, ссылки на изображения в них, а также ссылки
     */
    private static HandlerResponse getLastPosts(String groupName, User user) {
        try {
            return new HandlerResponse(Objects.requireNonNullElse(
                    vk.getLastPosts(5, groupName, user), TextResponse.NO_POSTS_IN_GROUP
            ));
        } catch (ApiTokenExtensionRequiredException e) {
            return new HandlerResponse(TextResponse.UPDATE_TOKEN);
        } catch (NoGroupException e) {
            return new HandlerResponse(TextResponse.NO_GROUP);
        } catch (ApiException | ClientException e) {
            return new HandlerResponse(TextResponse.VK_API_ERROR);
        }
    }

    /**
     * Метод возвращающий ответ при получении неизвестной команды
     *
     * @return ответ содержащий UNKNOWN_COMMAND
     */
    private static HandlerResponse getUnknownCommandResponse() {
        return new HandlerResponse(TextResponse.UNKNOWN_COMMAND);
    }
}
