package dj.arbuz.messages;

import dj.arbuz.handlers.messages.MessageHandlerResponse;
import org.junit.jupiter.api.Test;
import dj.arbuz.user.BotUser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().build(List.of(testUserId));
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertFalse((response.hasPostsMessages()));
        assertEquals(List.of(), response.getPostsMessages());
        assertFalse((response.hasAdditionalMessage()));
        assertNull(response.getAdditionalMessage());
        assertEquals(List.of(testUserId), response.getUsersSendResponseId());
    }
    /**
     * Метод тестирующий методы связанные с текстовым сообщением от бота
     */
    @Test
    public void testTextMessageConstructorsAndSettersAndGetters() {
        String expectedMessage = "some test message";
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().textMessage(expectedMessage).build(List.of(testUserId));
        assertTrue(response.hasTextMessage());
        assertEquals(expectedMessage, response.getTextMessage());
        assertFalse((response.hasPostsMessages()));
        assertEquals(List.of(), response.getPostsMessages());
        assertFalse((response.hasAdditionalMessage()));
        assertNull(response.getAdditionalMessage());
        assertEquals(List.of(testUserId), response.getUsersSendResponseId());
    }

    /**
     * Метод тестирующий методы связанный с созданием нового пользователя
     */
    @Test
    public void testUpdateUserConstructorsAndSettersAndGetters() throws ExecutionException, InterruptedException {
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder()
                .additionalMesage(CompletableFuture.supplyAsync(() -> "Hello"))
                .build(List.of(testUserId));
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertFalse((response.hasPostsMessages()));
        assertEquals(List.of(), response.getPostsMessages());
        assertTrue((response.hasAdditionalMessage()));
        assertEquals("Hello", response.getAdditionalMessage().get());
        assertEquals(List.of(testUserId), response.getUsersSendResponseId());
    }

    /**
     * Метод тестирующий методы связанные с новыми постами в ответе
     */
    @Test
    public void testNewPostsConstructorsAndSettersAndGetters() {
        List<String> newPosts = Arrays.asList("first post", "second post");
        MessageHandlerResponse response = MessageHandlerResponse.newBuilder().postsText(newPosts).build(List.of(testUserId));
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertTrue((response.hasPostsMessages()));
        assertNotNull(response.getPostsMessages());
        assertEquals(newPosts, response.getPostsMessages());
        assertFalse((response.hasAdditionalMessage()));
        assertNull(response.getAdditionalMessage());
        assertEquals(List.of(testUserId), response.getUsersSendResponseId());
    }
}
