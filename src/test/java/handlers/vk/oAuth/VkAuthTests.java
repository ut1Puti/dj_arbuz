package handlers.vk.oAuth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

/**
 * @author
 */
public class VkAuthTests {
    /**
     * Поле транспортного клиента
     */
    private final TransportClient transportClient = new HttpTransportClient();
    /**
     * Поле класс позволяющего работать с vk api
     */
    private final VkApiClient vk = new VkApiClient(transportClient);
    /**
     * Поле класса аутентификации пользователя в vk
     */
    private final VkAuth vkAuth = new VkAuth(
            vk, "src/test/java/dj/arbuz/resources/anonsrc/vkconfig.properties"
    );
}
