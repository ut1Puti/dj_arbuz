package handlers.vk.api.oAuth;

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
     * @param configPath - путь до файла с конфигурацией
     *                   файл должен содержать поля authUrl,
     *                   appId, clientSecret, redirectUri
     */
    VkAuthConfiguration(String configPath) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(configPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AUTH_URL = prop.getProperty("authUrl");
        wasElementFound(AUTH_URL, "authUrl", configPath);
        String appId = prop.getProperty("appId");
        wasElementFound(appId, "appId", configPath);
        APP_ID = Integer.parseInt(prop.getProperty("appId"));
        CLIENT_SECRET = prop.getProperty("clientSecret");
        wasElementFound(AUTH_URL, "clientSecret", configPath);
        SERVICE_CLIENT_SECRET = prop.getProperty("serviceClientSecret");
        wasElementFound(SERVICE_CLIENT_SECRET, "serviceClientSecret", configPath);
        REDIRECT_URL = prop.getProperty("redirectUri");
        wasElementFound(REDIRECT_URL, "redirectUri", configPath);
    }

    private void wasElementFound(String element, String elementName, String configPath) {
        if (element == null) {
            throw new RuntimeException("Нет" + elementName + " элемента в файле:" + configPath);
        }
    }
}
