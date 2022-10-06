package handlers.vkapi;

import java.io.FileInputStream;
import java.io.IOException;
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
        String appId = prop.getProperty("appId");
        if (appId == null) {
            throw new RuntimeException("No such element in file");
        }
        APP_ID = Integer.parseInt(appId);
        CLIENT_SECRET = prop.getProperty("clientSecret");
        REDIRECT_URL = prop.getProperty("redirectUri");
    }
}
