package dj.arbuz.socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.actors.ServiceActor;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import dj.arbuz.user.BotUser;

/**
 * Абстрактный класс для взаимодействия с авторизацией через vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see SocialNetworkAuth
 */
public abstract class AbstractVkAuth implements SocialNetworkAuth {
    /**
     * Конструктор - создает экземпляр класса
     */
    protected AbstractVkAuth() {
    }

    /**
     * Метод создающий пользователя приложения
     *
     * @return пользователя приложения
     * @see ServiceActor#ServiceActor(Integer, String, String)
     * @see VkAuthConfiguration#APP_ID
     * @see VkAuthConfiguration#CLIENT_SECRET
     * @see VkAuthConfiguration#SERVICE_CLIENT_SECRET
     */
    @Override
    public abstract ServiceActor createAppActor();

    /**
     * Метод создающий пользователя.
     * Создается с помощью Vk Java SDK, получая код с сервера
     *
     * @param userIdInBotSystem id пользователя в системе
     * @return нового пользователя, null если возникли проблемы при обращении к серверу, при ошибках на сервере
     * или при ошибке обращения к vk api
     * @see VkAuthConfiguration#APP_ID
     * @see VkAuthConfiguration#CLIENT_SECRET
     * @see VkAuthConfiguration#REDIRECT_URL
     */
    @Override
    public abstract BotUser createBotUser(String userIdInBotSystem) throws SocialNetworkException;
}
