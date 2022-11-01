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
public class MessageHandlerResponseTests {
    /**
     *
     */
    private final String testUserId = "testId";

    /**
     * Метод тестирующий создание пустого ответа
     */
    @Test
    public void testNullMessageConstructor() {
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().build(testUserId);
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
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().textMessage(expectedMessage).build(testUserId);
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
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().updateUser(createUser).build(testUserId);
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
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().postsText(newPosts).build(testUserId);
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertTrue((response.hasPostsMessages()));
        assertNotNull(response.getPostsMessages());
        assertEquals(newPosts, response.getPostsMessages());
        assertFalse((response.hasUpdateUser()));
        assertNull(response.getUpdateUser());
    }
}
