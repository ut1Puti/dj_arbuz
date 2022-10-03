package handlers.vkapi;

import com.vk.api.sdk.exceptions.ApiTokenExtensionRequiredException;
import com.vk.api.sdk.objects.groups.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий методы класса VkApiHandler
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkApiHandlerTests {
    /** Поле объекта обрабатывающего запросы к вк апи */
    private VkApiHandler vk = new VkApiHandler("src/test/resources/anonsrc/vkconfig.properties");
    /**  */
    private User testUser;

    /**
     * Метод проверяет корректную аутентификацию пользователя
     */
    @Test
    public void correctAuthTest(){
        assertNotNull(vk.getAuthURL());
        createUser();
        assertNotNull(testUser);
    }

    /**
     * Метод проверяет некорректную аутентификацию
     */
    @Test
    public void incorrectAuthTest(){
        VkApiHandler incorrectVk = new VkApiHandler("src/test/resources/testanonsrc/unknownfile.properties");
        assertNull(incorrectVk.getAuthURL());
        System.out.println(vk.getAuthURL());
        assertThrows(NullPointerException.class, (Executable) incorrectVk.createUser());
    }

    /**
     * Метод проверяет работу метода поиска группы
     * @throws ApiTokenExtensionRequiredException - возникает при истечении срока действия токена пользователя
     */
    @Test
    public void searchGroupTest() throws ApiTokenExtensionRequiredException {
        createUser();
        Group testGroup = vk.searchGroup("lida", testUser);
        assertEquals(testGroup.getId(), 147725517);
        assertEquals(testGroup.getScreenName(), "lidamudota");
    }

    /**
     * Метод создающий пользователя
     */
    private void createUser(){
        System.out.println(vk.getAuthURL());
        testUser = vk.createUser();
    }
}
