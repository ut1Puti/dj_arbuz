package dj.arbuz.socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import httpserver.server.HttpServer;
import dj.arbuz.user.BotUser;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Регулярное выражение для поиска кода аутентификации с помощью oAuth 2.0 и id пользователя для которого был получен код
     */
    private final Pattern codeAndTelegramIdRegex = Pattern.compile("code=(?<code>.+)&state=(?<id>.+)");

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param vkApiClient                    клиент vk
     * @param vkAppConfigurationJsonFilePath путь до json файла с конфигурацией
     */
    public VkAuth(VkApiClient vkApiClient, HttpServer httpServer, Path vkAppConfigurationJsonFilePath) {
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
    public String getAuthUrl(String userTelegramId) {
        return authConfiguration.AUTH_URL + "&state=" + userTelegramId;
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя.
     * Создается с помощью Vk Java SDK, получая код с сервера
     *
     * @param userSystemId - id пользователя в системе
     * @return нового пользователя, null если возникли проблемы при обращении к серверу, при ошибках на сервере
     * или при ошибке обращения к vk api
     * @see HttpServer#getHttpRequestParameters()
     * @see VkAuth#getCodeAndUserTelegramIfFromHttpGetParam(String)
     * @see VkAuthConfiguration#APP_ID
     * @see VkAuthConfiguration#CLIENT_SECRET
     * @see VkAuthConfiguration#REDIRECT_URL
     */
    @Override
    public BotUser createBotUser(String userSystemId) {
        String authCode = getAuthCodeFromHttpServer(userSystemId);

        if (authCode == null) {
            return null;
        }

        try {
            UserAuthResponse authResponse = vkApiClient.oAuth().userAuthorizationCodeFlow(
                            authConfiguration.APP_ID,
                            authConfiguration.CLIENT_SECRET,
                            authConfiguration.REDIRECT_URL,
                            authCode)
                    .execute();
            return new BotUser(authResponse.getUserId(), authResponse.getAccessToken(), userSystemId);
        } catch (ApiException | ClientException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Метод получающий код для аутентификации с помощью oAuth 2.0
     *
     * @param userSystemId id пользователя в системе
     * @return строку кода для аутентификации с помощью oAuth 2.0, {@code null} если не была получен верный код
     */
    private String getAuthCodeFromHttpServer(String userSystemId) {
        String authCode = null;
        for (int codeRequestsCounter = 0; codeRequestsCounter < 60; codeRequestsCounter++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }

            String httpRequestGetParameters = httpServer.getHttpRequestParameters();
            OAuthParams httpGetParam = getCodeAndUserTelegramIfFromHttpGetParam(httpRequestGetParameters);

            if (httpGetParam.userTelegramId.equals(userSystemId)) {
                authCode = httpGetParam.authCode;
                break;
            }
        }
        return authCode;
    }

    /**
     * Метод, который получает code из get параметров GET запроса на сервер
     *
     * @param httpRequestGetParameters - get параметры отправленные на сервер
     * @return {@code code}
     */
    private OAuthParams getCodeAndUserTelegramIfFromHttpGetParam(String httpRequestGetParameters) {
        Matcher matcher = codeAndTelegramIdRegex.matcher(httpRequestGetParameters);
        if (matcher.find()) {
            return new OAuthParams(matcher.group("code"), matcher.group("id"));
        }
        return new OAuthParams("", "");
    }

    /**
     * Хранит параметры для аутентификации с помощью oAuth 2.0
     *
     * @param authCode       - код oAuth 2.0
     * @param userTelegramId - id пользователя в телеграм
     */
    private record OAuthParams(String authCode, String userTelegramId) {
    }
}
