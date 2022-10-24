package bots;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import database.GroupsStorage;
import database.UserStorage;
import socialnetworks.socialnetwork.SocialNetwork;
import socialnetworks.vk.Vk;
import socialnetworks.vk.groups.VkGroups;
import socialnetworks.vk.oAuth.VkAuth;
import socialnetworks.vk.wall.VkWall;
import httpserver.server.HttpServer;

/**
 * Класс хранящий объекты необходимые для запуска бота
 *
 * @author Кедровских Олег
 * @version 0.5
 */
public class BotStartInstances {
    /**
     * Поле http сервера
     *
     * @see HttpServer
     */
    private final HttpServer httpServer;
    /**
     * Поле хранилища групп на которые оформленна подписка
     *
     * @see GroupsStorage
     */
    public final GroupsStorage groupsStorage;
    /**
     * Поле хранилища пользователей аутентифицированных через vk
     *
     * @see UserStorage
     */
    public final UserStorage userStorage;
    /**
     * Поле класса для взаимодействия с vk
     *
     * @see Vk
     */
    public final SocialNetwork vk;

    /**
     * Конструктор - создает экземпляр класса
     */
    public BotStartInstances(String vkAppConfigurationJsonFilePath) {
        this.httpServer = HttpServer.getInstance();

        if (httpServer == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        httpServer.start();
        this.groupsStorage = GroupsStorage.getInstance();
        this.userStorage = UserStorage.getInstance();
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vkApiClient = new VkApiClient(transportClient);
        VkGroups vkGroups = new VkGroups(vkApiClient);
        VkWall vkWall = new VkWall(vkApiClient);
        VkAuth vkAuth = new VkAuth(vkApiClient, httpServer, vkAppConfigurationJsonFilePath);
        this.vk = new Vk(vkGroups, vkWall, vkAuth);
    }

    /**
     * Метод останавливающий и сохраняющий состояние объектов хранимых в {@code BotStarterPack}
     */
    public void stop() {
        httpServer.stop();
        groupsStorage.saveToJsonFile();
        userStorage.saveToJsonFile();
    }
}
