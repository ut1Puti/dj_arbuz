package user;

import com.vk.api.sdk.client.actors.UserActor;

/**
 * Класс пользователя
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class User extends UserActor {
    /**
     * Поле id пользователя в телеграмме
     */
     private final String telegramId;

    /**
     * Конструктор от унаследованного класса
     *
     * @param vkId      - id пользователя в vk
     * @param accessToken - токен для доступа к vk api
     */
    public User(Integer vkId, String accessToken, String telegramId) {
        super(vkId, accessToken);
        this.telegramId = telegramId;
    }

    /**
     * Метод для получения id пользователя в телеграмме
     *
     * @return id пользователя в телеграмме
     */
    public String getTelegramId() {
        return telegramId;
    }
}
