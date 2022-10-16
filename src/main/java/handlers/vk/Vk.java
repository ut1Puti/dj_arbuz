package handlers.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import database.GroupsStorage;
import handlers.vk.groups.NoGroupException;
import handlers.vk.groups.VkGroups;
import handlers.vk.oAuth.VkAuth;
import handlers.vk.wall.VkWall;
import handlers.vk.groups.SubscribeStatus;
import user.CreateUser;
import user.User;

import java.util.List;
import java.util.Optional;

/**
 * Класс обрабатывающий запросы пользователя к Vk API
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 1.6
 */
public class Vk implements CreateUser {
    /**
     * Поле транспортного клиента
     */
    private final TransportClient transportClient = new HttpTransportClient();
    /**
     * Поле класс позволяющего работать с vk api
     */
    private final VkApiClient vk = new VkApiClient(transportClient);
    /**
     * Поле класса для взаимодействия с группами через vk api
     */
    private final VkGroups groups;
    /**
     * Поле класс для взаимодействия со стеной вк
     */
    private final VkWall wall;
    /**
     * Поле для аутентификации через vk
     */
    private final VkAuth oAuth;
    /**
     * Поле пользователя приложения в vk
     */
    private final ServiceActor vkApp;

    /**
     * Конструктор по пути до файла с конфигурацией приложения
     *
     * @param vkAppConfigurationFilePath - путь до файла с конфигурацией
     */
    public Vk(String vkAppConfigurationFilePath) {
        oAuth = new VkAuth(vk, vkAppConfigurationFilePath);
        groups = new VkGroups(vk);
        wall = new VkWall(vk, groups);
        vkApp = oAuth.createAppActor();
    }

    /**
     * Метод обертка возвращающий ссылку для аутентификации
     *
     * @return ссылку для аутентификации, если сервер недоступен, то это null
     */
    public String getAuthURL() {
        return oAuth.getAuthURL();
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя
     *
     * @return нового пользователя
     */
    @Override
    public User createUser(String userTelegramId) {
        return oAuth.createUser(userTelegramId);
    }

    /**
     * Метод получающий ссылку на группу в vk
     *
     * @param userReceivedGroupName - Название группы
     * @param userCalledMethod      - пользователя
     * @return возвращает ссылку на группу в vk
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public String getGroupURL(String userReceivedGroupName, User userCalledMethod)
            throws NoGroupException, ClientException, ApiException {
        return VkConstants.VK_ADDRESS + groups.searchGroup(userReceivedGroupName, userCalledMethod).getScreenName();
    }

    /**
     * Метод получающий id группы
     *
     * @param userReceivedGroupName - Название группы
     * @param userCalledMethod      - пользователя
     * @return возвращает id группы
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public String getGroupId(String userReceivedGroupName, User userCalledMethod)
            throws NoGroupException, ClientException, ApiException {
        return String.valueOf(groups.searchGroup(userReceivedGroupName, userCalledMethod).getId());
    }

    /**
     * Метод для подписки пользователя(сохранение в базу данных id пользователя в телеграмме и группы)
     *
     * @param userReceivedGroupName - Название группы
     * @param userCalledMethod      - пользователя
     * @return возвращает true - если пользователь только что подписался
     * false - если пользователь уже был подписан
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public SubscribeStatus subscribeTo(GroupsStorage groupBase, String userReceivedGroupName, User userCalledMethod)
            throws ApiException, NoGroupException, ClientException {
        return groups.subscribeTo(groupBase, userReceivedGroupName, userCalledMethod);
    }

    /**
     * Метод обертка для получения последних постов со стены
     *
     * @param amountOfPosts         - кол-во постов
     * @param userReceivedGroupName - имя группы
     * @param userCalledMethod      - пользователь вызвавший метод
     * @return возвращает последние amountOfPosts постов
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException         - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException          - возникает при ошибке обращения к vk api со стороны клиента
     * @throws IllegalArgumentException - возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                  Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     */
    public Optional<List<String>> getLastPosts(String userReceivedGroupName, int amountOfPosts, User userCalledMethod)
            throws NoGroupException, ClientException, ApiException {
        return wall.getLastPosts(userReceivedGroupName, amountOfPosts, userCalledMethod);
    }

    /**
     * Метод обертка для получения новых постов из группы в базе данных
     *
     * @param groupsStorage - база данных
     * @param groupScreenName - название группы в базе данных
     * @return список постов в группе в виде строк
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException          - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getNewPosts(GroupsStorage groupsStorage, String groupScreenName)
            throws ClientException, ApiException {
        return wall.getNewPosts(groupsStorage, groupScreenName, vkApp);
    }
}
