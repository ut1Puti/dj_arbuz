package handlers.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import handlers.vk.groups.SubscribeStatus;
import handlers.vk.oAuth.VkAuth;
import handlers.vk.groups.NoGroupException;
import handlers.vk.groups.VkGroups;
import handlers.vk.wall.VkWall;
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
     * Поле класс позволяющего работать с Vk SDK Java
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
     * @param configPath - путь до файла с конфигурацией
     */
    public Vk(String configPath) {
        oAuth = new VkAuth(vk, configPath);
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
    public User createUser(String telegramId) {
        return oAuth.createUser(telegramId);
    }

    /**
     * Метод получающий ссылку на группу в vk
     *
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return возвращает ссылку на группу в vk
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public String getGroupURL(String groupName, User callingUser) throws NoGroupException, ClientException, ApiException {
        return VkConstants.VK_ADDRESS + groups.searchGroup(groupName, callingUser).getScreenName();
    }

    /**
     * Метод получающий id группы
     *
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return возвращает id группы
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public String getGroupId(String groupName, User callingUser) throws NoGroupException, ClientException, ApiException {
        return String.valueOf(groups.searchGroup(groupName, callingUser).getId());
    }

    /**
     * Метод для подписки пользователя(сохранение в базу данных id пользователя в телеграмме и группы)
     *
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return возвращает true - если пользователь только что подписался
     * false - если пользователь уже был подписан
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public SubscribeStatus subscribeTo(String groupName, User callingUser) throws ApiException, NoGroupException, ClientException {
        return groups.subscribeTo(groupName, callingUser);
    }

    /**
     * Метод обертка для получения последних постов со стены
     *
     * @param amountOfPosts - кол-во постов
     * @param groupName     - имя группы
     * @param callingUser   - пользователь вызвавший метод
     * @return возвращает последние amountOfPosts постов
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getLastPosts(String groupName, int amountOfPosts, User callingUser) throws NoGroupException, ClientException, ApiException {
        return wall.getLastPosts(groupName, amountOfPosts, callingUser);
    }

    /**
     * Метод для получения новых постов со стены
     *
     * @param groupName         - название группы
     * @param dateOfLastGotPost - дата последнего просмотренного поста
     * @return возвращает непросмотренные посты
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getNewPosts(String groupName, int dateOfLastGotPost) throws NoGroupException, ClientException, ApiException {
        return wall.getNewPosts(groupName, dateOfLastGotPost, vkApp);
    }
}
