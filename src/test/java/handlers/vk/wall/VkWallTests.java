package handlers.vk.wall;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import handlers.vk.groups.NoGroupException;
import handlers.vk.groups.VkGroups;
import handlers.vk.oAuth.VkAuth;
import org.junit.jupiter.api.Test;
import user.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий класс VkWall
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkWallTests {
    /**
     * поле транспортного клиента
     */
    TransportClient client = new HttpTransportClient();
    /**
     * Поле класса позволяющего работать с vk api
     */
    VkApiClient vk = new VkApiClient(client);
    /**
     * Поле класса работающего с группами vk
     */
    VkGroups vkGroups = new VkGroups(vk);
    /**
     * Поле класса работающего со стеной в vk
     */
    VkWall vkWall = new VkWall(vk, vkGroups);
    /**
     * Поле класса аутентификации пользователя в vk
     */
    VkAuth vkAuth = new VkAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");

    /**
     * Метод тестирующий получение последних n постов со стены
     *
     * @throws NoGroupException     - ошибка, отсутствие группы по заданной подстроке
     * @throws ClientException      - ошибка со стороны клиента
     * @throws ApiException         - ошибка со стороны vk api
     * @throws InterruptedException - при прерывании потока
     */
    @Test
    public void getLastPosts() throws NoGroupException, ClientException, ApiException, InterruptedException {
        String authURL = vkAuth.getAuthURL();
        assertNotNull(authURL);
        System.out.println(authURL);
        User testUser = vkAuth.createUser();
        assertNotNull(testUser);
        Optional<List<String>> lastPosts = vkWall.getLastPosts("Молчат дома", 10, testUser);
        assertTrue(lastPosts.isPresent());
        Thread.sleep(10000);
        try {
            lastPosts = vkWall.getLastPosts("some not existing group", 101, testUser);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }
    }
}
