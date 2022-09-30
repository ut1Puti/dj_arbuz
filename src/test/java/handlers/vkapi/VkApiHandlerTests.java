package handlers.vkapi;

import com.vk.api.sdk.exceptions.ApiTokenExtensionRequiredException;
import com.vk.api.sdk.objects.groups.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class VkApiHandlerTests {
    private VkApiHandler vk = new VkApiHandler("src/test/resources/anonsrc/vkconfig.properties");
    private User testUser;

    @Test
    public void CorrectAuthTest(){
        assertNotNull(vk.getAuthURL());
        createUser();
        assertNotNull(testUser);
    }

    @Test
    public void IncorrectAuthTest(){
        VkApiHandler incorrectVk = new VkApiHandler("src/test/resources/testanonsrc/unknownfile.properties");
        assertNull(incorrectVk.getAuthURL());
        System.out.println(vk.getAuthURL());
        assertThrows(NullPointerException.class, (Executable) incorrectVk.createUser());
    }

    @Test
    public void SearchVerifiedGroupTest() throws ApiTokenExtensionRequiredException {
        createUser();
        Group testGroup = vk.searchVerifiedGroup("lida", testUser);
        assertEquals(testGroup.getId(), 147725517);
        assertEquals(testGroup.getScreenName(), "lidamudota");
    }

    private void createUser(){
        System.out.println(vk.getAuthURL());
        testUser = vk.createUser();
    }
}
