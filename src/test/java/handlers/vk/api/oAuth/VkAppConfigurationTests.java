package handlers.vk.api.oAuth;

import com.vk.api.sdk.client.actors.ServiceActor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий VkAppConfiguration
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkAppConfigurationTests {
    /** Поле содержащее конфигурацию приложения */
    private VkAuthConfiguration appConfiguration;

    /**
     * Проверяет правильность чтения данных из файла
     */
    @Test
    public void correctPathTest(){
        appConfiguration = new VkAuthConfiguration("src/test/resources/testanonsrc/vkconfigcorrect.properties");
        assertEquals(appConfiguration.AUTH_URL, "AUTH_URL");
        assertEquals(appConfiguration.APP_ID, 2000);
        assertEquals(appConfiguration.CLIENT_SECRET, "CLIENT_SECRET");
        assertEquals(appConfiguration.SERVICE_CLIENT_SECRET, "SERVICE_CLIENT_SECRET");
        assertEquals(appConfiguration.REDIRECT_URL, "REDIRECT_URI");
    }

    /**
     * Проверяет данные при ошибке чтения
     */
    @Test
    public void incorrectPathTest(){
        boolean error = false;
        try {
            appConfiguration = new VkAuthConfiguration("src/test/resources/testanonsrc/unknownfile.properties");
        } catch (RuntimeException e) {
            error = true;
        }
        assertTrue(error);
    }

    /**
     * Метод проверяющий appId на ошибки при прочтении данных из файла
     */
    @Test
    public void incorrectAppIdTest(){
        boolean error = false;
        try {
            appConfiguration = new VkAuthConfiguration("src/test/resources/testanonsrc/vkconfigincorrectappidname.properties");
        } catch (RuntimeException e) {
            error = true;
        }
        assertTrue(error);
    }

    /**
     * Метод проверяющий AUTH_URL, CLIENT_SECRET, REDIRECT_URL на ошибки при прочтении данных из файла
     */
    @Test
    public void incorrectStringsTest(){
        boolean error = false;
        try {
            appConfiguration = new VkAuthConfiguration("src/test/resources/testanonsrc/vkconfigincorrectstringsnames.properties");
        } catch (RuntimeException e) {
            error = true;
        }
        assertTrue(error);
    }
}
