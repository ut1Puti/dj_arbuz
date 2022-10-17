package handlers.vk.groups;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс тестирующий исключение отсутствия групп
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class NoGroupExceptionTests {
    /**
     * Метод тестирующий сообщение конструктора по названию группы
     */
    @Test
    public void testGroupNameConstructor() {
        String groupName = "some group";
        Exception e = new NoGroupException(groupName);
        assertEquals("Группы с названием " + groupName + " не существует", e.getMessage());
    }

    /**
     * Метод тестирующий сообщение конструктора по id группы
     */
    @Test
    public void testGroupIdConstructor() {
        int groupId = 100;
        Exception e = new NoGroupException(groupId);
        assertEquals("Группы с id " + groupId + " не существует", e.getMessage());
    }
}
