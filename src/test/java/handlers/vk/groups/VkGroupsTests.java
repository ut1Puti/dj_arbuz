package handlers.vk.groups;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.Group;
import dj.arbuz.handlers.vk.groups.NoGroupException;
import dj.arbuz.handlers.vk.groups.VkGroups;
import dj.arbuz.handlers.vk.oAuth.VkAuth;
import org.junit.jupiter.api.Test;
import dj.arbuz.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс тестирующий класс VkGroups
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkGroupsTests {
    /**
     * Поле транспортного клиента
     */
    private static final TransportClient transportClient = new HttpTransportClient();
    /**
     * Поле класс позволяющего работать с Vk SDK Java
     */
    static final VkApiClient vk = new VkApiClient(transportClient);

    /**
     * Метод тестирующий метод поиска групп
     *
     * @throws NoGroupException     - ошибка, отсутствие группы по заданной подстроке
     * @throws ClientException      - ошибка со стороны клиента
     * @throws ApiException         - ошибка со стороны vk api
     * @throws InterruptedException - при прерывании потока
     */
    @Test
    public void searchGroupTest() throws NoGroupException, ClientException, ApiException, InterruptedException {
        Thread.sleep(10000);
        VkGroups groups = new VkGroups(vk);
        VkAuth oAuth = new VkAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");
        String authUrl = oAuth.getAuthURL();
        System.out.println(authUrl);
        User user = oAuth.createUser();
        Group group = groups.searchGroup("Молчат дома", user);
        assertEquals(group.getScreenName(), "molchatdoma");
    }

    /**
     * Метод тестирующий поиск несуществующей группы
     *
     * @throws ClientException      - ошибка со стороны клиента
     * @throws ApiException         - ошибка со стороны vk api
     * @throws InterruptedException - при прерывании потока
     */
    @Test
    public void searchGroupWithNoGroupStringTest() throws ClientException, ApiException, InterruptedException {
        Thread.sleep(10000);
        VkGroups groups = new VkGroups(vk);
        VkAuth oAuth = new VkAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");
        String authUrl = oAuth.getAuthURL();
        System.out.println(authUrl);
        User user = oAuth.createUser();
        try {
            Group group = groups.searchGroup("оплавоплдывлдпдлыв", user);
        } catch (NoGroupException e) {
            assertEquals(e.getMessage(), "Группы с названием оплавоплдывлдпдлыв не существует");
        }
    }
}
