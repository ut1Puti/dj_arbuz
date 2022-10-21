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
 * @version 2.0
 */
public class HandlerResponseTests {
    /**
     * Метод тестирующий методы связанные с текстовым сообщением от бота
     */
    @Test
    public void testTextMessageConstructorsAndSettersAndGetters() {
        String expectedMessage = "some test message";
        MessageHandlerResponse response = new MessageHandlerResponse(expectedMessage);
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
        MessageHandlerResponse response = new MessageHandlerResponse(createUser);
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
        MessageHandlerResponse response = new MessageHandlerResponse(newPosts);
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertTrue((response.hasPostsMessages()));
        assertNotNull(response.getPostsMessages());
        assertEquals(newPosts, response.getPostsMessages());
        assertFalse((response.hasUpdateUser()));
        assertNull(response.getUpdateUser());
    }

    /**
     * Метод проверяющий работу метода append, который складывает текстовые сообщения двух ответов
     * Также тестируется работа setter'ов getter'ов
     */
    @Test
    public void appendTest() {
        MessageHandlerResponse response1 = new MessageHandlerResponse("Test");
        MessageHandlerResponse response2 = new MessageHandlerResponse("text");
        assertEquals(response1.appendTextMessage(response2).getTextMessage(), "Test\ntext");
    }
}
