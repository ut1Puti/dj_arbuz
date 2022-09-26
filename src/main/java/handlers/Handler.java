package handlers;

import bots.ConsoleBot;
import com.vk.api.sdk.exceptions.ApiTokenExtensionRequiredException;
import com.vk.api.sdk.objects.groups.Group;
import user.CreateUser;
import user.User;

// подумать как лучше обрабатывать exceptions
/**
 * Класс утилитных методов создающий ответы на сообщения пользователя
 * @author Кедровских Олег
 * @version 0.9
 */
public class Handler {
    /** Поле обработчика запросов к Vk API */
    private static HandlerVkApi vk  = new HandlerVkApi();
    /** Поле с информацией о доступных ф-циях бота */
    private static final String helpInfo = """
            Привет, я бот помогающий взаимодействовать с музыкальной индустрией.
            Я могу найти страницу исполнителя, выдав в качестве результата id или ссылку на его группу в вк.
            Чтоб запустить бота используйте /start и следуйте дальнейшим указаниям по аутентификации с помощью вк.
            Команда "/relogin" нужна для повторной аутентификации.
            Чтобы получить ссылку на подтвержденного исполнителя используйте команду 
            "/link имя артиста или его псевдоним".
            Чтобы получить id на подтвержденного исполнителя используйте команду
            "/id имя артиста или его псевдоним".
            чтобы остановить бота используйте 
            "/stop".
            Вы можете вызвать это сообщение еще раз использовав 
            "/help".""";

    /** Метод определяющий команды в сообщении пользователя и возвращающий ответ
     * @param message - сообщение пользователя
     * @param user - пользователь отправивший сообщение
     * @param callingBot - бот из которого был вызван метод
     * @return возвращает ответ на сообщение пользователя
     */
    public static HandlerResponse executeMessage(String message, User user, ConsoleBot callingBot) {
        String[] commandAndArg = message.split(" ", 2);
        switch (commandAndArg[0]) {
            case "/help" -> {
                return new HandlerResponse(helpInfo, null);
            }
            case "/start", "/relogin" -> {
                String authURL = vk.getAuthURL();
                CreateUser createUser = vk.getCreateUser();
                if (authURL == null || createUser == null){
                    return new HandlerResponse("Перейдите по ссылке, чтобы пройти аутентификацию:\n " + authURL + ".", createUser);
                }
                return new HandlerResponse("Ошибка при аутентификации. Повторите позже.", null);
            }
            case "/stop" -> {
                callingBot.stop();
                return new HandlerResponse("Остановлен.", null);
            }
            case "/link" -> {
                Group group;
                try {
                    group = vk.searchVerifiedGroup(commandAndArg[1], user);
                } catch (ApiTokenExtensionRequiredException e) {
                    return new HandlerResponse("Продлите токен с помощью команды /relogin.", null);
                }
                if (group == null) {
                    return new HandlerResponse("Я не смог найти верефицированную группу с таким названием.", null);
                }
                return new HandlerResponse("https://vk.com/" + group.getScreenName(), null);
            }
            case "/id" -> {
                Group group;
                try {
                    group = vk.searchVerifiedGroup(commandAndArg[1], user);
                } catch (ApiTokenExtensionRequiredException e) {
                    return new HandlerResponse("Продлите токен с помощью команды /relogin.", null);
                }
                if (group == null) {
                    return new HandlerResponse("Я не смог найти верефицированную группу с таким названием.", null);
                }
                return new HandlerResponse(String.valueOf(group.getId()), null);
            }
            case "/turn_on_notifications" -> {
                try {
                    vk.turnNotifications(true, commandAndArg[1], user);
                } catch (ApiTokenExtensionRequiredException e) {
                    return new HandlerResponse("Продлите токен с помощью команды /relogin", null);
                }
                return new HandlerResponse("not done", null);
            }
            case "/turn_off_notifications" -> {
                try {
                    vk.turnNotifications(false, commandAndArg[1], user);
                } catch (ApiTokenExtensionRequiredException e) {
                    return new HandlerResponse("Продлите токен с помощью команды /relogin", null);
                }
                return new HandlerResponse("not done", null);
            }
        }
        return new HandlerResponse("Неизвестная команда. Используйте /help, чтобы увидеть доступные", null);
    }
}
