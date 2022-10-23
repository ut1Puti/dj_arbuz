package handlers.vk.oAuth;

import com.google.gson.JsonSyntaxException;
import loaders.gson.GsonLoader;
import loaders.gson.GsonLoadable;

import java.io.IOException;
import java.util.Objects;

/**
 * Класс хранящий настройки vk приложения
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public class VkAuthConfiguration implements GsonLoadable {
    /**
     * Поле объекта, который выполняет чтение json файла и преобразования в объект
     *
     * @see GsonLoader
     */
    private static final GsonLoader<VkAuthConfiguration> VK_AUTH_CONFIGURATION_GSON_LOADER =
            new GsonLoader<>(VkAuthConfiguration.class);
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
    static VkAuthConfiguration loadVkAuthConfigurationFromJson(String vkAuthConfigurationJsonFilePath) {
        try {
            return VK_AUTH_CONFIGURATION_GSON_LOADER.loadFromJson(vkAuthConfigurationJsonFilePath);
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
