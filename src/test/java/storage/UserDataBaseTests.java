package storage;


import database.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.BotUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataBaseTests {
    private UserStorage userStorageTest;
    BotUser userTest;

    /**
     * Инициализация базы и одного тестового пользователя
     */
    @BeforeEach
    public void setUserStorge() {
        userStorageTest = UserStorage.getInstance();
        userTest = new BotUser(123, "123", "some Id");

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
        BotUser secondUser = new BotUser(150, "830", "second some text");
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

        BotUser secondUser = new BotUser(150, "830", "second some text");
        userStorageTest.addInfoUser(telegramId1, userTest);
        userStorageTest.addInfoUser(telegramId2, secondUser);
        assertTrue(userTest.equals(userStorageTest.getUser(telegramId1)) && secondUser.equals(userStorageTest.getUser(telegramId2)));
    }

}
