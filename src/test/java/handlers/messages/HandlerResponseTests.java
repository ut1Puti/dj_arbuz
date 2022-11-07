package handlers.messages;

import handlers.messages.HandlerResponse;
import org.junit.jupiter.api.Test;
import user.CreateUser;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий HandlerResponse
 */
public class HandlerResponseTests {
    /**
     * Метод тестирующий первый конструктор, то есть наличие строки с сообщение и интерфейса создающего пользователя
     * Также тестируется работа setter'ов getter'ов
     */
    @Test
    public void testFirstConstructorAndSettersAndGetters(){
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

    /**
     * Метод тестирующий второй конструктор, то есть наличие сообщения и отсутствие интерфейса создания пользователя
     * Также тестируется работа setter'ов getter'ов
     */
    @Test
    public void testSecondConstructorAndSettersAndGetters(){
        HandlerResponse response = new HandlerResponse("Test text");
        assertTrue(response.hasTextMessage());
        assertEquals(response.getTextMessage(), "Test text");
        assertFalse(response.hasUpdateUser());
        assertNull(response.getUpdateUser());
    }

    /**
     * Метод проверяющий третий конструктор, то наличие интерфейса создания пользователя и отсутствие текстового сообщения
     * Также тестируется работа setter'ов getter'ов
     */
    @Test
    public void testThirdConstructorAndSettersAndGetters(){
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

    /**
     * Метод проверяющий работу метода append, который складывает текстовые сообщения двух ответов
     * Также тестируется работа setter'ов getter'ов
     */
    @Test
    public void appendTest(){
        HandlerResponse response1 = new HandlerResponse("Test");
        HandlerResponse response2 = new HandlerResponse("text");
        assertEquals(response1.appendTextMessage(response2).getTextMessage(), "Test\ntext");
    }
}
