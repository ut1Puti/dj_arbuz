package socialnetworks.socialnetwork.oAuth;

import com.vk.api.sdk.client.actors.ServiceActor;
import user.BotUser;

/**
 * Интерфейс для аутентификации пользователя в социальной сети
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface SocialNetworkAuth<S, U> {
    /**
     * Метод для создания пользователя приложения социальной сети
     *
     * @return пользователя приложения социальной сети
     */
    S createAppActor();

    /**
     * Метод создающий пользователя, имеющего id в системе
     *
     * @param userIdInBotSystem - id пользователя в системе
     * @return пользователя содержащего id в социальной сети и в бот,
     * token - ключ доступа к api социальной сети с использованием аккаунта пользователя,
     * либо же {@code null} если не удается создать пользователя по какой-то причине
     */
    U createBotUser(String userIdInBotSystem);

    /**
     * Метод возвращающий ссылку для аутентификации в социальной сети
     *
     * @return ссылку для аутентификации в социальной сети, если ссылка по какой-то причине отсутствует возвращает {@code null}
     */
    String getAuthUrl();
}
