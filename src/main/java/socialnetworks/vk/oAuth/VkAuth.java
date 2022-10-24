package socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import httpserver.server.HttpServer;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import user.User;

/**
 * Класс для аутентификации пользователей
 *
 * @author Кедровских Олег
 * @version 1.2
 * @see SocialNetworkAuth
 */
public class VkAuth implements SocialNetworkAuth {
    /**
     * Поле класс позволяющего работать с vk api
     *
     * @see VkApiClient
     */
    private final VkApiClient vkApiClient;
    /**
     * Поле сервера получающего токены пользователя и переправляющего пользователей на tg бота
     *
     * @see HttpServer
     */
    private final HttpServer httpServer;
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
    public VkAuth(VkApiClient vkApiClient, HttpServer httpServer, String vkAppConfigurationJsonFilePath) {
        this.vkApiClient = vkApiClient;
        this.httpServer = httpServer;
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
    public String getAuthUrl() {
        return authConfiguration.AUTH_URL;
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя.
     * Создается с помощью Vk Java SDK, получая код с сервера
     *
     * @param systemUserId - id пользователя в системе
     * @return нового пользователя, null если возникли проблемы при обращении к серверу, при ошибках на сервере
     * или при ошибке обращения к vk api
     * @see HttpServer#getHttpRequestParameters()
     * @see VkAuth#getAuthCodeFromHttpRequest(String)
     * @see VkAuthConfiguration#APP_ID
     * @see VkAuthConfiguration#CLIENT_SECRET
     * @see VkAuthConfiguration#REDIRECT_URL
     */
    @Override
    public User createUser(String systemUserId) {
        String httpRequestGetParameters = httpServer.getHttpRequestParameters();

        if (httpRequestGetParameters == null) {
            return null;
        }

        String authCode = getAuthCodeFromHttpRequest(httpRequestGetParameters);
        try {
            UserAuthResponse authResponse = vkApiClient.oAuth().userAuthorizationCodeFlow(
                            authConfiguration.APP_ID,
                            authConfiguration.CLIENT_SECRET,
                            authConfiguration.REDIRECT_URL,
                            authCode)
                    .execute();
            return new User(authResponse.getUserId(), authResponse.getAccessToken(), systemUserId);
        } catch (ApiException | ClientException e) {
            return null;
        }
    }

    /**
     * Метод, который получает code из get параметров GET запроса на сервер
     *
     * @param httpRequestGetParameters - get параметры отправленные на сервер
     * @return {@code code}
     */
    private String getAuthCodeFromHttpRequest(String httpRequestGetParameters) {
        final char newParameterStartSymbol = '&';
        final String parameterName = "code=";
        int startParameterValueIndex = httpRequestGetParameters.lastIndexOf(parameterName) + parameterName.length();
        StringBuilder authCodeBuilder = new StringBuilder();
        for (int i = startParameterValueIndex; i < httpRequestGetParameters.length(); i++) {

            if (httpRequestGetParameters.charAt(i) == newParameterStartSymbol) {
                break;
            }

            authCodeBuilder.append(httpRequestGetParameters.charAt(i));
        }
        return authCodeBuilder.toString();
    }
}
