package handlers;

import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class MessageHandlerTests {
    @Test
    public void TestAllCommands(){
        User user = null;
        HandlerResponse response;

        response = MessageHandler.executeMessage("/help", user, null);
        assertEquals(response.getTextMessage(), TextResponse.HELP_INFO);
        assertNull(response.getUpdateUser());

        response = MessageHandler.executeMessage("/abrakadabra", user, null);
        assertEquals(response.getTextMessage(), TextResponse.NOT_AUTHED_USER);
        assertNull(response.getUpdateUser());

        response = MessageHandler.executeMessage("/start", user, null);
        assertNotEquals(response.getTextMessage(), TextResponse.AUTH_ERROR);
        System.out.println(response.getTextMessage());
        assertNotNull(response.getUpdateUser());
        user = response.getUpdateUser().createUser();
        assertNotNull(user);

        response = MessageHandler.executeMessage("/link lida", user, null);
        assertEquals(response.getTextMessage(), TextResponse.VK_ADDRESS + "lidamudota");
        assertNull(response.getUpdateUser());

        response = MessageHandler.executeMessage("/id lida", user, null);
        assertEquals(response.getTextMessage(), "147725517");
        assertNull(response.getUpdateUser());

        response = MessageHandler.executeMessage("/abrakadabra", user, null);
        assertEquals(response.getTextMessage(), TextResponse.UNKNOWN_COMMAND);
        assertNull(response.getUpdateUser());
    }
}
