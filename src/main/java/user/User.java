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
     * Конструктор от унаследованного класса
     *
     * @param vkId      - id пользователя в вк
     * @param accessToken - токен для доступа к vk api
     */
    public User(Integer vkId, String accessToken) {
        super(vkId, accessToken);
    }
}
