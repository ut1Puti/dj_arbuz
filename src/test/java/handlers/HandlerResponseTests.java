package handlers;

import org.junit.jupiter.api.Test;
import user.CreateUser;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class HandlerResponseTests {
    @Test
    public void TestFirstConstructor(){
        HandlerResponse response = new HandlerResponse("Test text", new CreateUser() {
            @Override
            public User createUser() {
                return null;
            }
        });
        assertTrue(response.hasTextMessage());
        assertEquals(response.getTextMessage(), "Test text");
        assertTrue(response.hasUpdateUser());
        assertNull(response.getUpdateUser().createUser());
    }

    @Test
    public void TestSecondConstructor(){
        HandlerResponse response = new HandlerResponse("Test text");
        assertTrue(response.hasTextMessage());
        assertEquals(response.getTextMessage(), "Test text");
        assertFalse(response.hasUpdateUser());
        assertNull(response.getUpdateUser());
    }

    @Test
    public void TestThirdConstructor(){
        HandlerResponse response = new HandlerResponse(new CreateUser() {
            @Override
            public User createUser() {
                return null;
            }
        });
        assertFalse(response.hasTextMessage());
        assertNull(response.getTextMessage());
        assertTrue(response.hasUpdateUser());
        assertNull(response.getUpdateUser().createUser());
    }
}
