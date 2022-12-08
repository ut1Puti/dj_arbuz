package dj.arbuz.socialnetworks.vk.oAuth;

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
public abstract class AbstractVkAuth implements SocialNetworkAuth {
    /**
     * Конструктор - создает экземпляр класса
     */
    protected AbstractVkAuth() {
    }

    @Override
    public abstract ServiceActor createAppActor();

    @Override
    public abstract BotUser createBotUser(String userIdInBotSystem);
}
