package dj.arbuz.socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import dj.arbuz.socialnetworks.vk.oAuth.OAuthCodeQueue.MessageQueuePuller;
import dj.arbuz.user.BotUser;

import java.nio.file.Path;

/**
 * Класс для аутентификации пользователей
 *
 * @author Кедровских Олег
 * @version 2.0
 * @see AbstractVkAuth
 */
public final class VkAuth extends AbstractVkAuth {
    /**
     * Поле класс позволяющего работать с vk api
     *
     * @see VkApiClient
     */
    private final VkApiClient vkApiClient;
    private final OAuthCodeQueue oAuthCodeQueue;
    /**
     * Поле с конфигурации данных для аутентификации пользователь и приложения
     *
     * @see VkAuthConfiguration
     */
    private final VkAuthConfiguration authConfiguration;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param vkApiClient                    клиент vk
     * @param vkAppConfigurationJsonFilePath путь до json файла с конфигурацией
     */
    public VkAuth(VkApiClient vkApiClient, OAuthCodeQueue oAuthCodeQueue, Path vkAppConfigurationJsonFilePath) {
        this.vkApiClient = vkApiClient;
        this.oAuthCodeQueue = oAuthCodeQueue;
        this.authConfiguration = VkAuthConfiguration.loadVkAuthConfigurationFromJson(vkAppConfigurationJsonFilePath);
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
    public ServiceActor createAppActor() {
        return new ServiceActor(
                authConfiguration.APP_ID, authConfiguration.CLIENT_SECRET, authConfiguration.SERVICE_CLIENT_SECRET
        );
    }

    /**
     * Метод возвращающий ссылку для аутентификации
     *
     * @return ссылку для аутентификации, если сервер недоступен, то это null
     * @see VkAuthConfiguration#AUTH_URL
     */
    @Override
    public String getAuthUrl(String userTelegramId) {
        return authConfiguration.AUTH_URL + "&state=" + userTelegramId;
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя.
     * Создается с помощью Vk Java SDK, получая код с сервера
     *
     * @param userTelegramId id пользователя в системе
     * @return нового пользователя, null если возникли проблемы при обращении к серверу, при ошибках на сервере
     * или при ошибке обращения к vk api
     * @see VkAuthConfiguration#APP_ID
     * @see VkAuthConfiguration#CLIENT_SECRET
     * @see VkAuthConfiguration#REDIRECT_URL
     */
    @Override
    public BotUser createBotUser(String userTelegramId) {
        String oAuthCode = null;
        try (MessageQueuePuller oAuthCodeQueuePuller = oAuthCodeQueue.subscribe(userTelegramId)) {
            oAuthCode = oAuthCodeQueuePuller.pollMessage();
        } catch (Exception e) {
            return null;
        }

        try {
            UserAuthResponse authResponse = vkApiClient.oAuth().userAuthorizationCodeFlow(
                            authConfiguration.APP_ID,
                            authConfiguration.CLIENT_SECRET,
                            authConfiguration.REDIRECT_URL,
                            oAuthCode)
                    .execute();
            return new BotUser(authResponse.getUserId(), authResponse.getAccessToken(), userTelegramId);
        } catch (ApiException | ClientException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
