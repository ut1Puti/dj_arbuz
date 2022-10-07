package handlers.vk.api.groups;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.Group;
import handlers.vk.api.oAuth.VkApiAuth;
import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VkApiGroupsTests {
    /** Поле транспортного клиента */
    private static final TransportClient transportClient = new HttpTransportClient();
    /** Поле класс позволяющего работать с Vk SDK Java */
    static final VkApiClient vk = new VkApiClient(transportClient);

    @Test
    public void searchGroupTest() throws NoGroupException, ClientException, ApiException {
        VkApiGroups groups = new VkApiGroups(vk);
        VkApiAuth oAuth = new VkApiAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");
        String authUrl = oAuth.getAuthURL();
        System.out.println(authUrl);
        User user = oAuth.createUser();
        Group group = groups.searchGroup("Молчат дома", user);
        assertEquals(group.getScreenName(), "molchatdoma");
    }

    @Test
    public void searchGroupWithNoGroupStringTest() throws ClientException, ApiException {
        VkApiGroups groups = new VkApiGroups(vk);
        VkApiAuth oAuth = new VkApiAuth(vk, "src/test/resources/anonsrc/vkconfig.properties");
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
