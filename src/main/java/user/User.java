package user;

import com.vk.api.sdk.client.actors.UserActor;

/**
 * Класс пользователя
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class User extends UserActor {
    private String userTelegramId;

    /**
     * Конструктор от унаследованного класса
     *
     * @param userId      - id пользователя в вк
     * @param accessToken - токен для доступа к vk api
     */

    public User(Integer userId, String accessToken) {
        super(userId, accessToken);
    }

    /*public void setUserTelegramId(String userTelegramId) {
        this.userTelegramId = userTelegramId;
    }
     */
}
