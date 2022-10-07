package handlers.vk.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Класс тестирующий VkAppConfiguration
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkAppConfigurationTests {
    /** Поле содержащее конфигурацию приложения */
    private VkAppConfiguration appConfiguration;

    /**
     * Проверяет правильность чтения данных из файла
     */
    @Test
    public void correctPathTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/vkconfigcorrect.properties");
        assertEquals(appConfiguration.AUTH_URL, "AUTH_URL");
        assertEquals(appConfiguration.APP_ID, 2000);
        assertEquals(appConfiguration.CLIENT_SECRET, "CLIENT_SECRET");
        assertEquals(appConfiguration.REDIRECT_URL, "REDIRECT_URI");
    }

    /**
     * Проверяет данные при ошибке чтения
     */
    @Test
    public void incorrectPathTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/unknownfile.properties");
        assertNull(appConfiguration.AUTH_URL);
        assertNull(appConfiguration.APP_ID);
        assertNull(appConfiguration.CLIENT_SECRET);
        assertNull(appConfiguration.REDIRECT_URL);
    }

    /**
     * Метод проверяющий appId на ошибки при прочтении данных из файла
     */
    @Test
    public void incorrectAppIdTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/vkconfigincorrectappidname.properties");
        assertEquals(appConfiguration.AUTH_URL, "AUTH_URL");
        assertNull(appConfiguration.APP_ID);
        assertEquals(appConfiguration.CLIENT_SECRET, "CLIENT_SECRET");
        assertEquals(appConfiguration.REDIRECT_URL, "REDIRECT_URI");
    }

    /**
     * Метод проверяющий AUTH_URL, CLIENT_SECRET, REDIRECT_URL на ошибки при прочтении данных из файла
     */
    @Test
    public void incorrectStringsTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/vkconfigincorrectstringsnames.properties");
        assertNull(appConfiguration.AUTH_URL);
        assertEquals(appConfiguration.APP_ID, 100);
        assertNull(appConfiguration.CLIENT_SECRET);
        assertNull(appConfiguration.REDIRECT_URL);
    }
}
