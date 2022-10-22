package handlers.vk.oAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Класс хранящий настройки vk приложения
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public class VkAuthConfiguration {
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
        FileInputStream stream;
        try {
            stream = new FileInputStream(vkAppConfigurationFilePath);
            prop.load(stream);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("Файла по пути " + vkAppConfigurationFilePath + " не найдено");
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
        REDIRECT_URL = prop.getProperty("redirectUrl");
        wasElementFound(REDIRECT_URL, "redirectUrl", vkAppConfigurationFilePath);
    }

    /**
     * Метод проверяющий, был ли найден элемент в файле
     *
     * @param fileFindElement            - найденный элемент
     * @param fileSearchingElementName   - ключ, по которому искался элемент
     * @param vkAppConfigurationFilePath - путь до файла из которого читались данных
     */
    private void wasElementFound(String fileFindElement, String fileSearchingElementName,
                                 String vkAppConfigurationFilePath) {
        if (fileFindElement == null) {
            throw new RuntimeException("Нет " + fileSearchingElementName + " элемента в файле: " + vkAppConfigurationFilePath);
        }

    }

    /**
     * Метод вычисляющий хэш экземляра класса
     *
     * @return хэш экземпляра
     */
    @Override
    public int hashCode() {
        return (APP_ID + AUTH_URL + CLIENT_SECRET + SERVICE_CLIENT_SECRET + REDIRECT_URL).hashCode();
    }

    /**
     * Метод проверяющий равенство {@code obj} и {@code VkAuthConfiguration}
     *
     * @param obj - сравниваемый объект
     * @return true если объекты равны по полям, false если объекты не равны по полям
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof VkAuthConfiguration anotherVkAuthConfiguration)) {
            return false;
        }

        return AUTH_URL.equals(anotherVkAuthConfiguration.AUTH_URL) &&
                Objects.equals(APP_ID, anotherVkAuthConfiguration.APP_ID) &&
                CLIENT_SECRET.equals(anotherVkAuthConfiguration.CLIENT_SECRET) &&
                SERVICE_CLIENT_SECRET.equals(anotherVkAuthConfiguration.SERVICE_CLIENT_SECRET) &&
                REDIRECT_URL.equals(anotherVkAuthConfiguration.REDIRECT_URL);
    }
}
