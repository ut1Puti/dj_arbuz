package storage;


import database.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataBaseTests {
    private UserStorage userStorageTest;
    User userTest;

    /**
     * Инициализация базы и одного тестового пользователя
     */
    @BeforeEach
    public void setUserStorge() {
        userStorageTest = UserStorage.getInstance();
        userTest = new User(123, "123", "some Id");

    }

    /**
     * Проверка на добавление пользователя в базу
     */
    @Test
    public void testAddNewUser() {
        String telegramId = "some id";
        userStorageTest.addInfoUser(telegramId, userTest);
        assertEquals(userTest, userStorageTest.getUser(telegramId));
    }

    /**
     * Проверка удаления старого юзера и добавление нового по одному айдишнику
     */
    @Test
    public void testTwoUsersForOneId() {
        String telegramId = "some id";
        User secondUser = new User(150, "830", "second some text");
        userStorageTest.addInfoUser(telegramId, userTest);
        userStorageTest.addInfoUser(telegramId, secondUser);
        assertEquals(secondUser, userStorageTest.getUser(telegramId));
    }
    /**
    Проверка добавления двух разных пользователей
     */
    @Test
    public void testAddTwoNotEqualsUsersForNotEqualsID() {
        String telegramId1 = "some id";
        String telegramId2 = "some id2";

        User secondUser = new User(150, "830", "second some text");
        userStorageTest.addInfoUser(telegramId1, userTest);
        userStorageTest.addInfoUser(telegramId2, secondUser);
        assertTrue(userTest.equals(userStorageTest.getUser(telegramId1)) && secondUser.equals(userStorageTest.getUser(telegramId2)));
    }

}
