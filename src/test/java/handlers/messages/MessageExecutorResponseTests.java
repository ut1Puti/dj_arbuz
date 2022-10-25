package handlers.messages;

import org.junit.jupiter.api.Test;
import user.CreateUser;
import user.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий HandlerResponse
 *
 * @author Кедровских Олег
 * @version 3.0
 */
public class MessageExecutorResponseTests {
    /**
     * Метод тестирующий создание пустого ответа
     */
    @Test
    public void testNullMessageConstructor() {
        MessageExecutorResponse response = MessageExecutorResponse.newBuilder().build();
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertFalse((response.hasPostsMessages()));
        assertNull(response.getPostsMessages());
        assertFalse((response.hasUpdateUser()));
        assertNull(response.getUpdateUser());
    }
    /**
     * Метод тестирующий методы связанные с текстовым сообщением от бота
     */
    @Test
    public void testTextMessageConstructorsAndSettersAndGetters() {
        String expectedMessage = "some test message";
        MessageExecutorResponse response = MessageExecutorResponse.newBuilder().textMessage(expectedMessage).build();
        assertTrue(response.hasTextMessage());
        assertEquals(expectedMessage, response.getTextMessage());
        assertFalse((response.hasPostsMessages()));
        assertNull(response.getPostsMessages());
        assertFalse((response.hasUpdateUser()));
        assertNull(response.getUpdateUser());
    }

    /**
     * Метод тестирующий методы связанный с созданием нового пользователя
     */
    @Test
    public void testUpdateUserConstructorsAndSettersAndGetters() {
        CreateUser createUser = new CreateUser() {
            @Override
            public User createUser(String systemUserId) {
                return null;
            }
        };
        MessageExecutorResponse response = MessageExecutorResponse.newBuilder().updateUser(createUser).build();
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertFalse((response.hasPostsMessages()));
        assertNull(response.getPostsMessages());
        assertTrue((response.hasUpdateUser()));
        assertNotNull(response.getUpdateUser());
    }

    /**
     * Метод тестирующий методы связанные с новыми постами в ответе
     */
    @Test
    public void testNewPostsConstructorsAndSettersAndGetters() {
        List<String> newPosts = Arrays.asList("first post", "second post");
        MessageExecutorResponse response = MessageExecutorResponse.newBuilder().postsText(newPosts).build();
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertTrue((response.hasPostsMessages()));
        assertNotNull(response.getPostsMessages());
        assertEquals(newPosts, response.getPostsMessages());
        assertFalse((response.hasUpdateUser()));
        assertNull(response.getUpdateUser());
    }
}
