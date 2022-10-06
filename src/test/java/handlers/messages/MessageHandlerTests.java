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
    public void testAllCommands(){
        Thread testThread = new Thread(() -> {
            User user = null;
            HandlerResponse response;
            response = MessageHandler.executeMessage("/help", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.HELP_INFO);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/abrakadabra", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.NOT_AUTHED_USER);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/link lida", user, null);
            assertEquals(response.getTextMessage(), "https://vk.com/lidamudota");
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/id lida", user, null);
            assertEquals(response.getTextMessage(), "147725517");
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/subscribe triplesixdelete", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.SUBSCRIBE);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/subscribe triplesixdelete", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.ALREADY_SUBSCRIBER);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/abrakadabra", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.UNKNOWN_COMMAND);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/subscribe triplesixdelete", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.SUBSCRIBE);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            response = MessageHandler.executeMessage("/subscribe triplesixdelete", user, null);
            assertEquals(response.getTextMessage(), BotTextResponse.SUBSCRIBE);
            assertNull(response.getUpdateUser());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
