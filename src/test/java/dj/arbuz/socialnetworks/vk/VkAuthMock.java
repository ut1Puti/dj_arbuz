package dj.arbuz.socialnetworks.vk;

import com.vk.api.sdk.client.actors.ServiceActor;
import dj.arbuz.socialnetworks.vk.oAuth.AbstractVkAuth;
import dj.arbuz.user.BotUser;

/**
 * Тестовый класс авторизации в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class VkAuthMock extends AbstractVkAuth {
    /**
     * Поле ссылки для авторизации
     */
    private final String authUrl = "AUTH_URL";
    /**
     * Поле секретного ключа приложения
     */
    private final String clientSecret = "CLIENT_SECRET";
    /**
     * Поле ключа доступа к vk api
     */
    private final String accessToken = "ACCESS_TOKEN";

    /**
     * Метод создающий пользователя приложения vk
     *
     * @return нового пользователя приложения vk
     */
    @Override
    public ServiceActor createAppActor() {
        final int serviceActorId = -1;
        return new ServiceActor(serviceActorId, clientSecret, accessToken);
    }

    /**
     * Метод создающий нового пользователя vk
     *
     * @param userIdInBotSystem - id пользователя в системе
     * @return новый пользователь авторизовавшийся в vk
     */
    @Override
    public BotUser createBotUser(String userIdInBotSystem) {
        final int botUserId = 1;
        return new BotUser(botUserId, accessToken, userIdInBotSystem);
    }

    /**
     * Метод возвращающий ссылку для авторизации пользователя
     *
     * @return ссылку на страницу с авторизацией
     */
    @Override
    public String getAuthUrl(String userTelegramId) {
        return authUrl;
    }
}
