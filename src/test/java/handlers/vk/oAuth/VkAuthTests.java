package handlers.vk.oAuth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import user.User;

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
     * Поле класса аутентификации пользователя в vk
     */
    private static final VkAuth vkAuth = new VkAuth(
            vk, "src/test/resources/anonsrc/vkconfig.properties"
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
        String authUrl = vkAuth.getAuthURL();
        assertEquals(expectedAuthUrl, authUrl);
    }

    /**
     * Метод для тестирования случая при котором пользователь не согласился принимать разрешения доступа бота
     */
    @AfterAll
    @Test
    public static void testCreateUserWithRejectionOfStates() {
        User user = vkAuth.createUser("some telegram id");
        assertNull(user);
    }
}
