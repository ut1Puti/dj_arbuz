package socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.actors.ServiceActor;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import user.BotUser;

/**
 * Абстрактный класс для взаимодействия с авторизацией через vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see SocialNetworkAuth
 */
public abstract class AbstractVkAuth implements SocialNetworkAuth<ServiceActor, BotUser> {
    /**
     * Конструктор - создает экземпляр класса
     */
    protected AbstractVkAuth() {
    }
}
