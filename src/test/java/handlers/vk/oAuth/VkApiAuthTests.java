package handlers.vk.oAuth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Класс для тестирований аутентификации в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkApiAuthTests {
    /** Поле транспортного клиента */
    private static final TransportClient transportClient = new HttpTransportClient();
    /** Поле класс позволяющего работать с Vk SDK Java */
    static final VkApiClient vk = new VkApiClient(transportClient);

    @Test
    public void serviceActorAuthTest() {
        VkAuth oAuth = new VkAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");
        ServiceActor vkApp = oAuth.createAppActor();
        assertEquals(vkApp.getId(), 51434490);
    }

    @Test
    public void createUserTest() {
        VkAuth oAuth = new VkAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");
        String authUrl = oAuth.getAuthURL();
        assertNotNull(authUrl);
        System.out.println(authUrl);
        User user = oAuth.createUser();
        assertNotNull(user);
    }
}
