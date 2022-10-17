package handlers.vk.oAuth;

import handlers.vk.oAuth.VkAuthConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий VkAppConfiguration
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public class VkAppConfigurationTests {
    /**
     * Поле содержащее конфигурацию приложения
     */
    private VkAuthConfiguration appConfiguration;

    /**
     * Метод проверяет правильность обработки данных конструктором при правильном пути и правильных ключах
     */
    @Test
    public void testConstructorWithCorrectConfigurationFilePathAndDataKeys() {
        appConfiguration = new VkAuthConfiguration("src/test/resources/testanonsrc/vkconfig.properties");
        assertEquals("AUTH_URL", appConfiguration.AUTH_URL);
        assertEquals(2000, appConfiguration.APP_ID);
        assertEquals("CLIENT_SECRET", appConfiguration.CLIENT_SECRET);
        assertEquals("SERVICE_CLIENT_SECRET", appConfiguration.SERVICE_CLIENT_SECRET);
        assertEquals("REDIRECT_URL", appConfiguration.REDIRECT_URL);
    }

    /**
     * Метод проверяет обработку конструктором некоректного пути до файла
     */
    @Test
    public void testConstructorWithIncorrectFilePath() {
        String incorrectVkAppConfigurationPath = "src/test/resources/testanonsrc/unknownfile.properties";
        String expectedExceptionMessage = "Файла по пути " + incorrectVkAppConfigurationPath + " не найдено";
        try {
            appConfiguration = new VkAuthConfiguration(incorrectVkAppConfigurationPath);
        } catch (RuntimeException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    /**
     * Метод проверяющий обработку в конструкторе, неправильно записаных в файле ключей
     *
     * @param incorrectKeyName         - неправильный ключ
     * @param incorrectDataKeyFilePath - путь до файла с неправильным ключом
     */
    @ParameterizedTest(name = "Некоректный ключ {0}, путь до файла {1}")
    @MethodSource("incorrectDataKeyNameData")
    public void testConstructorWithIncorrectDataKeyName(String incorrectKeyName, String incorrectDataKeyFilePath) {
        String expectedExceptionMessage = "Нет " + incorrectKeyName + " элемента в файле: " + incorrectDataKeyFilePath;
        try {
            appConfiguration = new VkAuthConfiguration(incorrectDataKeyFilePath);
        } catch (RuntimeException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    /**
     * Метод создающий данные для тестов чтения данных по неправильному ключу
     *
     * @return stream некоректно записаных в файле ключей и путь до этих файлов
     */
    private static Stream<Arguments> incorrectDataKeyNameData() {
        return Stream.of(
                Arguments.of("authUrl", "src/test/resources/testanonsrc/incorrectAuthUrl.properties"),
                Arguments.of("appId", "src/test/resources/testanonsrc/incorrectAppId.properties"),
                Arguments.of("clientSecret", "src/test/resources/testanonsrc/incorrectClientSecret.properties"),
                Arguments.of("serviceClientSecret", "src/test/resources/testanonsrc/incorrectServiceClientSecret.properties"),
                Arguments.of("redirectUrl", "src/test/resources/testanonsrc/incorrectRedirectUrl.properties")
        );
    }
}
