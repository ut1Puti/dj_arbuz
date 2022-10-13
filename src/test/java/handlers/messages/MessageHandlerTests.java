package handlers.messages;

import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
import org.junit.jupiter.api.Test;
import dj.arbuz.user.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий работу класса MessageHandler
 *
 * @author Кедровских Олег
 * @version 1.5
 */
public class MessageHandlerTests {
    /**
     * Метод проверяющий корректность работы всех команд бота
     */
    @Test
    public void testAllCommands() {
        User user = null;
        MessageHandlerResponse response;
        response = MessageHandler.executeMessage("/help", "-1", null);
        assertEquals(response.getTextMessage(), BotTextResponse.HELP_INFO);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/abrakadabra", "-1", null);
        assertEquals(response.getTextMessage(), BotTextResponse.NOT_AUTHED_USER);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/start", "-1", null);
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

        response = MessageHandler.executeMessage("/link lida", "-1", null);
        assertEquals(response.getTextMessage(), "https://vk.com/lidamudota");
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/link hfjksghfjagjk", "-1", null);
        assertEquals(response.getTextMessage(), "Группы с названием hfjksghfjagjk не существует");
        assertNull(response.getUpdateUser());

        response = MessageHandler.executeMessage("/id lida", "-1", null);
        assertEquals(response.getTextMessage(), "147725517");
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/subscribe triplesixdelete", "-1", null);
        assertEquals(response.getTextMessage(), BotTextResponse.SUBSCRIBE);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/subscribe triplesixdelete", "-1", null);
        assertEquals(response.getTextMessage(), BotTextResponse.ALREADY_SUBSCRIBER);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/abrakadabra", "-1", null);
        assertEquals(response.getTextMessage(), BotTextResponse.UNKNOWN_COMMAND);
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/get_five_posts triplesixdelete", "-1", null);
        assertNotNull(response.getTextMessage());
        assertNull(response.getUpdateUser());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response = MessageHandler.executeMessage("/get_five_last_posts", "-1", null);
        assertEquals(response.getTextMessage(), BotTextResponse.NO_POSTS_IN_GROUP);
    }
}
