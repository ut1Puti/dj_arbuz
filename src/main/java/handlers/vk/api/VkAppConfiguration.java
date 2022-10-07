package handlers.vk.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Класс хранящий настройки vk приложения
 * @author Кедровских Олег
 * @version 1.0
 */
class VkAppConfiguration {
    /** Поле ссылки для аутентификации */
    final String AUTH_URL;
    /** Поле id vk приложения */
    final Integer APP_ID;
    /** Поле ключа vk приложения */
    final String CLIENT_SECRET;
    /** Поле кода авторизации приложения */
    final String SERVICE_CLIENT_SECRET;
    /** Поле ссылки на которую перекинут пользователя после аутентификации */
    final String REDIRECT_URL;

    /**
     * Конструктор по пути до файла с конфигурацией приложения
     * @param configPath - путь до файла с конфигурацией
     *                     файл должен содержать поля authUrl,
     *                     appId, clientSecret, redirectUri
     */
    VkAppConfiguration(String configPath) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(configPath));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        AUTH_URL = prop.getProperty("authUrl");
        wasElementFound(AUTH_URL, configPath);
        String appId = prop.getProperty("appId");
        wasElementFound(appId, configPath);
        APP_ID = Integer.parseInt(prop.getProperty("appId"));
        CLIENT_SECRET = prop.getProperty("clientSecret");
        wasElementFound(AUTH_URL, configPath);
        SERVICE_CLIENT_SECRET = prop.getProperty("serviceClientSecret");
        wasElementFound(SERVICE_CLIENT_SECRET, configPath);
        REDIRECT_URL = prop.getProperty("redirectUri");
        wasElementFound(REDIRECT_URL, configPath);
    }

    private void wasElementFound(String element, String configPath) {
        if (element == null) {
            throw new RuntimeException("No such element in file:" + configPath);
        }
    }
}
