package handlers.vk.api;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.Group;
import database.Storage;
import handlers.vk.api.groups.NoGroupException;
import handlers.vk.api.groups.VkApiGroups;
import handlers.vk.api.wall.VkApiWall;
import httpserver.HttpServer;
import user.CreateUser;
import user.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Класс обрабатывающий запросы пользователя к Vk API
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 1.4
 */
public class VkApiHandler implements CreateUser {
    /** Поле транспортного клиента */
    private static final TransportClient transportClient = new HttpTransportClient();
    /** Поле класс позволяющего работать с Vk SDK Java */
    static final VkApiClient vk = new VkApiClient(transportClient);
    /** Поле сервера получающего токены пользователя и переправляющего пользователей на tg бота */
    private HttpServer httpServer = null;
    /** Поле хранилища данных о группах и пользователях */
    private Storage dataBase = null;
    /** Поле конфигурации vk приложения */
    private final VkAppConfiguration appConfiguration;
    /** Поле класса для взаимодействия с группами через vk api */
    private final VkApiGroups groups;
    /** Поле класс для взаимодействия со стеной вк */
    private final VkApiWall wall;
    /** Поле пользователя приложения в вк */
    private final ServiceActor vkApp;

    /**
     * Конструктор по пути до файла с конфигурацией приложения
     *
     * @param configPath - путь до файла с конфигурацией
     */
    public VkApiHandler(String configPath) {
        appConfiguration = new VkAppConfiguration(configPath);
        groups = new VkApiGroups(vk);
        wall = new VkApiWall(vk, groups);
        vkApp = new ServiceActor(
                appConfiguration.APP_ID, appConfiguration.CLIENT_SECRET, appConfiguration.SERVICE_CLIENT_SECRET
        );
    }

    /**
     * Метод возвращающий ссылку для аутентификации
     *
     * @return ссылку для аутентификации, если сервер недоступен, то это null
     */
    public String getAuthURL() {
        try {
            httpServer = HttpServer.getInstance();
        } catch (IOException e){
            return null;
        }
        return appConfiguration.AUTH_URL;
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя
     * Создается с помощью Vk Java SDK, получая код с сервера
     *
     * @return интерфейс для создания пользователя
     */
    @Override
    public User createUser() {
        String httpRequestGetParameters;
        try {
            httpServer = HttpServer.getInstance();
            httpRequestGetParameters = httpServer.getHttpRequestGetParametrs();
        } catch (IOException e) {
            return null;
        }

        if (httpRequestGetParameters == null){
            return null;
        }

        String authCode = getAuthCodeFromHttpRequest(httpRequestGetParameters);
        try {
            UserAuthResponse authResponse = vk.oAuth()
                    .userAuthorizationCodeFlow(
                            appConfiguration.APP_ID,
                            appConfiguration.CLIENT_SECRET,
                            appConfiguration.REDIRECT_URL,
                            authCode)
                    .execute();
            return new User(authResponse.getUserId(), authResponse.getAccessToken());
        } catch (ApiException | ClientException e) {
            return null;
        }
    }

    /**
     * Метод получающий ссылку на группу в вк
     *
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return возвращает ссылку на группу в вк
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public String getGroupURL(String groupName, User callingUser) throws NoGroupException, ClientException, ApiException {
        return VkApiConsts.VK_ADDRESS + groups.searchGroup(groupName, callingUser).getScreenName();
    }

    /**
     * Метод получающий id группы
     *
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return возвращает id группы
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public String getGroupId(String groupName, User callingUser) throws NoGroupException, ClientException, ApiException {
        return String.valueOf(groups.searchGroup(groupName, callingUser).getId());
    }

    /**
     * Метод для подписки пользователя(сохранение в базу данных айди и группы)
     *
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return возвращает true - если пользователь только что подписался
     *                    false - если пользователь уже был подписан
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public boolean subscribeTo(String groupName, User callingUser) throws ApiException, NoGroupException, ClientException {
        Group userFindGroup = groups.searchGroup(groupName, callingUser);

        if (dataBase == null) {
            dataBase = Storage.getInstance();
        }

        return dataBase.addInfoToGroup(userFindGroup.getScreenName(), String.valueOf(callingUser.getId()));
    }

    /**
     * Метод обертка для получения последних постов со стены
     *
     * @param amountOfPosts - кол-во постов
     * @param groupName - имя группы
     * @param callingUser - пользователь вызвавший метод
     * @return возвращает последние amountOfPosts постов
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getLastPosts(String groupName, int amountOfPosts, User callingUser) throws NoGroupException, ClientException, ApiException {
        return wall.getLastPosts(groupName, amountOfPosts, callingUser);
    }

    /**
     * Метод обертка для получения новых постов со стены
     *
     * @param groupName - название группы
     * @param dateOfLastGotPost - дата последнего просмотренного поста
     * @return возвращает непросмотренные посты
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getNewPosts(String groupName, int dateOfLastGotPost) throws NoGroupException, ClientException, ApiException {
        return wall.getNewPosts(groupName, dateOfLastGotPost, vkApp);
    }

    /**
     * Метод, который получает code из get параметров GET запроса на сервер
     *
     * @param httpRequestGetParameters - get параметры отправленные на сервер
     * @return code
     */
    private String getAuthCodeFromHttpRequest(String httpRequestGetParameters) {
        StringBuilder authCodeBuilder = new StringBuilder();
        for (int i = httpRequestGetParameters.lastIndexOf("code=") + "code=".length(); i < httpRequestGetParameters.length(); i++){

            if (httpRequestGetParameters.charAt(i) == '&') {
                break;
            }

            authCodeBuilder.append(httpRequestGetParameters.charAt(i));
        }
        return authCodeBuilder.toString();
    }
}
