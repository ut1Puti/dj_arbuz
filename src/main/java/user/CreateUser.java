package user;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.io.IOException;

//сделать javadoc
//подумать над exceptions
@FunctionalInterface
public interface CreateUser {
    public User createUser();
}
