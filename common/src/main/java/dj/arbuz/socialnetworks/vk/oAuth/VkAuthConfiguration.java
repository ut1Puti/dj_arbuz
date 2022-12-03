package dj.arbuz.socialnetworks.vk.oAuth;

import com.google.gson.JsonSyntaxException;
import loaders.gson.GsonLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Класс хранящий настройки vk приложения
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public final class VkAuthConfiguration {
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
     * Конструктор - создает экземпляр класса
     *
     * @param authUrl             ссылка для аутентификации пользователя
     * @param appId               id приложения
     * @param clientSecret        ключ доступа приложения к vk api
     * @param serviceClientSecret ключ доступа пользователя приложения к vk api
     * @param redirectUrl         ссылка на страницу, на которую попадет пользователь после аутентификации
     */
    public VkAuthConfiguration(String authUrl, int appId, String clientSecret, String serviceClientSecret,
                               String redirectUrl) {
        this.AUTH_URL = authUrl;
        this.APP_ID = appId;
        this.CLIENT_SECRET = clientSecret;
        this.SERVICE_CLIENT_SECRET = serviceClientSecret;
        this.REDIRECT_URL = redirectUrl;
    }

    /**
     * Метод для создания конфигурации из json файла
     *
     * @param vkAuthConfigurationJsonFilePath путь до json файла с конфигурацией приложения
     * @return {@code VkAuthConfiguration} на основе json файла
     */
    static VkAuthConfiguration loadVkAuthConfigurationFromJson(Path vkAuthConfigurationJsonFilePath) {
        try {
            GsonLoader<VkAuthConfiguration> vkAuthConfigurationGsonLoader = new GsonLoader<>();
            VkAuthConfiguration loadedConf = vkAuthConfigurationGsonLoader.loadFromJson(vkAuthConfigurationJsonFilePath, VkAuthConfiguration.class);
            if (loadedConf.APP_ID == null || loadedConf.AUTH_URL == null ||
                    loadedConf.REDIRECT_URL == null || loadedConf.CLIENT_SECRET == null ||
                    loadedConf.SERVICE_CLIENT_SECRET == null) {
                throw new IOException("Поля объекта не могут быть null");
            }
            return loadedConf;
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод вычисляющий хэш экземляра класса
     *
     * @return хэш экземпляра
     */
    @Override
    public int hashCode() {
        return APP_ID;
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

        return Objects.equals(AUTH_URL, anotherVkAuthConfiguration.AUTH_URL) &&
                Objects.equals(APP_ID, anotherVkAuthConfiguration.APP_ID) &&
                Objects.equals(CLIENT_SECRET, anotherVkAuthConfiguration.CLIENT_SECRET) &&
                Objects.equals(SERVICE_CLIENT_SECRET, anotherVkAuthConfiguration.SERVICE_CLIENT_SECRET) &&
                Objects.equals(REDIRECT_URL, anotherVkAuthConfiguration.REDIRECT_URL);
    }
}
