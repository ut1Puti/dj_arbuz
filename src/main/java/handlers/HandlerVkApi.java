package handlers;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ApiTokenExtensionRequiredException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import httpserver.HttpServer;
import user.CreateUser;
import user.User;

import java.io.IOException;
import java.util.List;

import static com.vk.api.sdk.objects.groups.Fields.VERIFIED;

// закончить уведомления
// подумать над exceptions
/**
 * Класс обрабатывающий запросы пользователя к Vk API
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 0.6
 */
public class HandlerVkApi {
    /** Поле транспортного клиента */
    private final TransportClient transportClient = new HttpTransportClient();
    /** Поле класс позволяющего работать с Vk SDK Java */
    private final VkApiClient vk = new VkApiClient(transportClient);
    /** Поле сервера получающего токены пользователя и переправляющего их на tg бота */
    private HttpServer server = null;

    /**
     * Метод возвращающий ссылку для аутентификации
     * @return ссылку для аутентификации, если сервер недоступен, то это null
     */
    public String getAuthURL(){
        try {
            if (server == null){
                server = HttpServer.getInstance();
            }
        } catch (IOException e){
            return null;
        }
        return VkApiConfig.AUTH_URL;
    }

    /**
     * Метод возвращаюший интерфейс для создания пользователя
     * @return интерфейс для создания пользователя
     */
    public CreateUser getCreateUser(){
        return () -> {
            try {
                if (server == null) {
                    server = HttpServer.getInstance();
                }
                String code = getCodeFromHttpRequest(server.getCode());
                UserAuthResponse authResponse = vk.oAuth()
                        .userAuthorizationCodeFlow(
                                VkApiConfig.APP_ID,
                                VkApiConfig.CLIENT_SECRET,
                                VkApiConfig.REDIRECT_URI,
                                code)
                        .execute();
                return new User(authResponse.getUserId(), authResponse.getAccessToken());
            } catch (ApiException | IOException | ClientException e) {
                    return null;
            }
        };
    }

    /**
     * Метод, который ищет все группы по запросу
     * @param groupName - запрос
     * @param user - пользователь сделавщий запрос
     * @return список групп полученных по запросу
     * @throws ApiTokenExtensionRequiredException - возникает если токен пользователя истек
     */
    public List<Group> searchGroups(String groupName, User user) throws ApiTokenExtensionRequiredException {
        try {
            return vk.groups().search(user, groupName)
                    .offset(0).count(3)
                    .execute()
                    .getItems();
        } catch (ApiException | ClientException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Метод, который ищет подтвержденные группы по запросу
     * @param groupName - запрос
     * @param user - пользователь сделавщий запрос
     * @return верифицированную группу
     *         если групп оказалось больше одной возвращает с большим числом подписчиков, то есть первую из списка
     *         если верифицированная группа не нашлась, возвращает null
     * @throws ApiTokenExtensionRequiredException - возникает если токен пользователя истек
     */
    public Group searchVerifiedGroup(String groupName, User user) throws ApiTokenExtensionRequiredException {
        List<Group> foundGroups = searchGroups(groupName, user);
        if (foundGroups == null) {
            return null;
        }
        for (Group foundGroup : foundGroups) {
            try {
                List<GetByIdObjectLegacyResponse> foundVerifiedGroups = vk.groups().getByIdObjectLegacy(user)
                        .groupId(String.valueOf(foundGroup.getId()))
                        .fields(VERIFIED)
                        .execute();
                return foundVerifiedGroups.get(0);
            } catch (ApiException | ClientException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * работаем
     * @param turn - показывает надо ли включить или выключить уведомления
     * @param groupName - запрос
     * @param user - пользователь сделваший запрос
     * @throws ApiTokenExtensionRequiredException - возникает если токен пользователя истек
     */
    public void turnNotifications(boolean turn, String groupName, User user) throws ApiTokenExtensionRequiredException {
        Group group = searchVerifiedGroup(groupName, user);
        try {
            vk.wall().get(user).domain(group.getScreenName()).offset(0).count(3).execute();
        } catch (ClientException | ApiException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * получает code из get параметров GET запроса на сервер
     * @param httpRequestGetParametrs - get параметры отправленные на сервер
     * @return code
     */
    private String getCodeFromHttpRequest(String httpRequestGetParametrs){
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = httpRequestGetParametrs.lastIndexOf("code=") + "code=".length(); i < httpRequestGetParametrs.length(); i++){
            if (httpRequestGetParametrs.charAt(i) == '&'){
                break;
            }
            codeBuilder.append(httpRequestGetParametrs.charAt(i));
        }
        return codeBuilder.toString();
    }
}
