package dj.arbuz.handlers.vk.oAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс хранящий настройки vk приложения
 *
 * @author Кедровских Олег
 * @version 2.0
 */
class VkAuthConfiguration {
    /**
     * Поле ссылки для аутентификации
     */
    final String AUTH_URL;
    /**
     * Поле id vk приложения
     */
    final Integer APP_ID;
    /**
     * Поле ключа vk приложения
     */
    final String CLIENT_SECRET;
    /**
     * Поле кода авторизации приложения
     */
    final String SERVICE_CLIENT_SECRET;
    /**
     * Поле ссылки на которую перекинут пользователя после аутентификации
     */
    final String REDIRECT_URL;

    /**
     * Конструктор по пути до файла с конфигурацией приложения
     *
     * @param vkAppConfigurationFilePath - путь до файла с конфигурацией
     *                                   файл должен содержать поля authUrl, appId, clientSecret, redirectUri
     */
    VkAuthConfiguration(String vkAppConfigurationFilePath) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(vkAppConfigurationFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AUTH_URL = prop.getProperty("authUrl");
        wasElementFound(AUTH_URL, "authUrl", vkAppConfigurationFilePath);
        String appId = prop.getProperty("appId");
        wasElementFound(appId, "appId", vkAppConfigurationFilePath);
        APP_ID = Integer.parseInt(prop.getProperty("appId"));
        CLIENT_SECRET = prop.getProperty("clientSecret");
        wasElementFound(AUTH_URL, "clientSecret", vkAppConfigurationFilePath);
        SERVICE_CLIENT_SECRET = prop.getProperty("serviceClientSecret");
        wasElementFound(SERVICE_CLIENT_SECRET, "serviceClientSecret", vkAppConfigurationFilePath);
        REDIRECT_URL = prop.getProperty("redirectUri");
        wasElementFound(REDIRECT_URL, "redirectUri", vkAppConfigurationFilePath);
    }

    /**
     * Метод проверяющий, был ли найден элемент в файле
     *
     * @param fileFindElement          - найденный элемент
     * @param fileSearchingElementName - ключ, по которому искался элемент
     * @param configPath               - путь до файла из которого читались данные
     * @throws RuntimeException - если элемент в данном файле отсутсвовал
     */
    private void wasElementFound(String fileFindElement, String fileSearchingElementName, String configPath) {
        if (fileFindElement == null) {
            throw new RuntimeException("Нет" + fileSearchingElementName + " элемента в файле:" + configPath);
        }
    }
}
