package user;

import com.vk.api.sdk.client.actors.UserActor;

/**
 * Класс пользователя
 * @author Кедровских Олег
 * @version 0.1
 */
public class User extends UserActor {

    public User(Integer userId, String accessToken) {
        super(userId, accessToken);
    }
}
