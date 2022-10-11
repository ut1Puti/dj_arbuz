package handlers.messages;

import bots.BotTextResponse;
import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий работу класса MessageHandler
 */
public class MessageHandlerTests {
    /**
     * Метод проверяющий корректность работы всех команд бота
     */
    @Test
    public void testAllCommands() {
        User user = null;
        MessageHandlerResponse response;
        response = MessageHandler.executeMessage("/help", user, null);
        assertEquals(response.getTextMessage(), BotTextResponse.HELP_INFO);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/abrakadabra", user, null);
        assertEquals(response.getTextMessage(), BotTextResponse.NOT_AUTHED_USER);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/start", user, null);
        assertNotEquals(response.getTextMessage(), BotTextResponse.AUTH_ERROR);
        System.out.println(response.getTextMessage());
        assertNotNull(response.getUpdateUser());
        user = response.getUpdateUser().createUser();
        assertNotNull(user);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/link lida", user, null);
        assertEquals(response.getTextMessage(), "https://vk.com/lidamudota");
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/link hfjksghfjagjk", user, null);
        assertEquals(response.getTextMessage(), "Группы с названием hfjksghfjagjk не существует");
        assertNull(response.getUpdateUser());

        response = MessageHandler.executeMessage("/id lida", user, null);
        assertEquals(response.getTextMessage(), "147725517");
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/subscribe triplesixdelete", user, null);
        assertEquals(response.getTextMessage(), BotTextResponse.SUBSCRIBE);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/subscribe triplesixdelete", user, null);
        assertEquals(response.getTextMessage(), BotTextResponse.ALREADY_SUBSCRIBER);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/abrakadabra", user, null);
        assertEquals(response.getTextMessage(), BotTextResponse.UNKNOWN_COMMAND);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/get_five_posts triplesixdelete", user, null);
        assertNotNull(response.getTextMessage());
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/get_five_last_posts", user, null);
        assertEquals(response.getTextMessage(), BotTextResponse.NO_POSTS_IN_GROUP);
    }
}
