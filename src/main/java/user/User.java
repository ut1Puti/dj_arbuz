package user;

import com.vk.api.sdk.client.actors.UserActor;

public class User extends UserActor {

    public User(Integer userId, String accessToken) {
        super(userId, accessToken);
    }
}
