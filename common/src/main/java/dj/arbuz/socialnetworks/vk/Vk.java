package dj.arbuz.socialnetworks.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import httpserver.server.HttpServer;
import dj.arbuz.socialnetworks.vk.groups.VkGroups;
import dj.arbuz.socialnetworks.vk.oAuth.VkAuth;
import dj.arbuz.socialnetworks.vk.wall.VkWall;

/**
 * Класс обрабатывающий запросы пользователя к Vk API
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 3.0
 * @see AbstractVk
 */
public final class Vk extends AbstractVk {
    /**
     * Поле клиента для соединения с vk api
     */
    private static final TransportClient transportClient = new HttpTransportClient();
    /**
     * Поле для взаимодействия с vk java sdk
     */
    private static final VkApiClient vkApiClient = new VkApiClient(transportClient);

    /**
     * Конструктор - создает экземпляр класса
     */
    public Vk() {
        super(
                new VkAuth(vkApiClient, HttpServer.getInstance(), VkConfigPaths.VK_CONFIG_PATH),
                new VkGroups(vkApiClient),
                new VkWall(vkApiClient)
        );
    }
}
