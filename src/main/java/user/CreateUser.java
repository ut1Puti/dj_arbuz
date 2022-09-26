package user;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.io.IOException;

@FunctionalInterface
public interface CreateUser {
    public User createUser();
}
