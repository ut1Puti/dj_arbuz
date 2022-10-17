package handlers.vk.oAuth;

import com.vk.api.sdk.actions.OAuth;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import httpserver.server.HttpServer;
import user.CreateUser;
import user.User;

/**
 * Класс для аутентификации пользователей
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkAuth extends OAuth implements CreateUser {
    /**
     * Поле сервера получающего токены пользователя и переправляющего пользователей на tg бота
     */
    private HttpServer httpServer = null;
    /**
     * Поле с конфигурации данных для аутентификации пользователь и приложения
     */
    private final VkAuthConfiguration authConfiguration;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param client                     - клиент vk
     * @param vkAppConfigurationFilePath - путь до файла с конфигурацией
     */
    public VkAuth(VkApiClient client, String vkAppConfigurationFilePath) {
        super(client);
        authConfiguration = new VkAuthConfiguration(vkAppConfigurationFilePath);
    }

    /**
     * Метод создающий пользователя приложения
     *
     * @return пользователя приложения
     */
    public ServiceActor createAppActor() {
        return new ServiceActor(
                authConfiguration.APP_ID, authConfiguration.CLIENT_SECRET, authConfiguration.SERVICE_CLIENT_SECRET
        );
    }

    /**
     * Метод возвращающий ссылку для аутентификации
     *
     * @return ссылку для аутентификации, если сервер недоступен, то это null
     */
    public String getAuthURL() {
        httpServer = HttpServer.getInstance();

        if (httpServer == null) {
            return null;
        }

        return authConfiguration.AUTH_URL;
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя.
     * Создается с помощью Vk Java SDK, получая код с сервера
     *
     * @return нового пользователя
     */
    @Override
    public User createUser(String userTelegramId) {
        httpServer = HttpServer.getInstance();

        if (httpServer == null) {
            return null;
        }

        String httpRequestGetParameters = httpServer.getHttpRequestParameters();

        if (httpRequestGetParameters == null) {
            return null;
        }

        String authCode = getAuthCodeFromHttpRequest(httpRequestGetParameters);
        try {
            UserAuthResponse authResponse = userAuthorizationCodeFlow(
                    authConfiguration.APP_ID,
                    authConfiguration.CLIENT_SECRET,
                    authConfiguration.REDIRECT_URL,
                    authCode)
                    .execute();
            return new User(authResponse.getUserId(), authResponse.getAccessToken(), userTelegramId);
        } catch (ApiException | ClientException e) {
            return null;
        }
    }

    /**
     * Метод, который получает code из get параметров GET запроса на сервер
     *
     * @param httpRequestGetParameters - get параметры отправленные на сервер
     * @return code
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
