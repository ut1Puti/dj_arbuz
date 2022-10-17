package dj.arbuz.handlers.vk.groups;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import handlers.vk.groups.VkGroups;

/**
 * Класс для тестирования класса для взаимодействия с группами через vk api
 *
 * @author
 * @version
 */
public class VkGroupsTests {
    /**
     * Поле транспортного клиента
     */
    private final TransportClient transportClient = new HttpTransportClient();
    /**
     * Поле класс позволяющего работать с vk api
     */
    private final VkApiClient vk = new VkApiClient(transportClient);
    /**
     * Поле класса взаимодействующего с группами через vk api
     */
    private final VkGroups vkGroups = new VkGroups(vk);
}
