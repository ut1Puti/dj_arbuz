package dj.arbuz.socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import dj.arbuz.user.BotUser;

/**
 * Абстрактный класс для взаимодействия с авторизацией через vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see SocialNetworkAuth
 */
public abstract class AbstractVkAuth implements SocialNetworkAuth<ServiceActor, BotUser, GroupActor> {
    /**
     * Конструктор - создает экземпляр класса
     */
    protected AbstractVkAuth() {
    }
}
