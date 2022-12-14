package socialnetworks.vk.oAuth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import httpserver.server.HttpServer;
import org.junit.jupiter.api.Test;
import user.BotUser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для тестирования класса авторизации пользователей в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkAuthTests {
    /**
     * Поле транспортного клиента
     */
    private static final TransportClient transportClient = new HttpTransportClient();
    /**
     * Поле класс позволяющего работать с vk api
     */
    private static final VkApiClient vk = new VkApiClient(transportClient);
    /**
     * Поле сервера для авторизации пользователей
     */
    private static final HttpServer httpServer = HttpServer.getInstance();
    /**
     * Поле класса аутентификации пользователя в vk
     */
    private static final VkAuth vkAuth = new VkAuth(
            vk, httpServer,"src/test/resources/anonsrc/vk_config.json"
    );

    /**
     * Метод для тестирования создания пользователя приложения vk
     */
    @Test
    public void testCreateAppActor() {
        ServiceActor appUser = vkAuth.createAppActor();
        assertNotNull(appUser);
    }

    /**
     * Метод для тестирования ссылки для авторизации пользователя
     */
    @Test
    public void testAuthUrl() {
        String expectedAuthUrl = "https://oauth.vk.com/authorize?client_id=51434490&display=page&redirect_uri=http://localhost:8080/redirect.html&scope=270336&response_type=code&v=5.131";
        String authUrl = vkAuth.getAuthUrl();
        assertEquals(expectedAuthUrl, authUrl);
    }

    /**
     * Метод для тестирования случая при котором пользователь не согласился принимать разрешения доступа бота
     */
    @Test
    public void testCreateUserWithRejectionOfStates() {
        BotUser user = vkAuth.createBotUser("some telegram id");
        assertNull(user);
    }
}
