package dj.arbuz.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Класс тестирующий класс данных связаных с группой
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class GroupRelatedDataTests {
    /**
     * Поле данных связанных с группой
     */
    private GroupRelatedData groupRelatedData;

    /**
     * Метод создающий данные перед каждым тестом
     */
    @BeforeEach
    public void setGroupRelatedData() {
        groupRelatedData = new GroupRelatedData();
    }

    /**
     * Метод проверяющий меотд добавления пользователя
     */
    @Test
    public void testAddNewSubscriber() {
        String newUserId = "some user id";
        groupRelatedData.addNewSubscriber(newUserId);
        assertEquals(List.of(newUserId), groupRelatedData.getSubscribedUsersId());
    }

    /**
     * Метод проверяющий добавление нескольких пользователей
     */
    @Test
    public void testAddNewSubscribers() {
        List<String> newUsersId = Arrays.asList("user1", "user2", "user3");
        groupRelatedData.addNewSubscribers(newUsersId);
        assertEquals(newUsersId, groupRelatedData.getSubscribedUsersId());
    }

    /**
     * Метод проверяющий получение даты последнего поста
     */
    @Test
    public void testGetLastPostDate() {
        assertEquals(0, groupRelatedData.getLastPostDate());
    }

    /**
     * Метод проверяющий метод обновления последнего поста
     */
    @Test
    public void testUpdateLastPostDate() {
        Random random = new Random();
        int lastPostDate = random.nextInt();
        GroupRelatedData newGroupData = groupRelatedData.updateLastPostDate(lastPostDate);
        assertEquals(lastPostDate, newGroupData.getLastPostDate());
    }

    /**
     * Метод тестирующий корректность добавления пользователей с одинаковым id
     */
    @Test
    public void testAddUsersWithSameId() {
        String newUserId1 = "user1";
        String newUserId2 = "user1";
        groupRelatedData.addNewSubscribers(Arrays.asList(newUserId1, newUserId2));
        assertEquals(1, groupRelatedData.getSubscribedUsersId().size());
    }

    /**
     * Метод тестирующий метод проверяющий является ли пользователь подписчиком группы
     */
    @Test
    public void testContainsUserId() {
        String newUserId = "100";
        groupRelatedData.addNewSubscriber(newUserId);
        assertTrue(groupRelatedData.contains(newUserId));
    }
}
