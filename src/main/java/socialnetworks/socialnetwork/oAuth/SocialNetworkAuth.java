package socialnetworks.socialnetwork.oAuth;

import com.vk.api.sdk.client.actors.ServiceActor;
import user.CreateUser;

/**
 * Интерфейс для аутентификации пользователя в социальной сети
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see CreateUser
 */
public interface SocialNetworkAuth extends CreateUser {
    /**
     * Метод для создания пользователя приложения социальной сети
     *
     * @return пользователя приложения социальной сети
     */
    ServiceActor createAppActor();

    /**
     * Метод возвращающий ссылку для аутентификации в социальной сети
     *
     * @return ссылку для аутентификации в социальной сети, если ссылка по какой-то причине отсутствует возвращает {@code null}
     */
    String getAuthUrl();
}
