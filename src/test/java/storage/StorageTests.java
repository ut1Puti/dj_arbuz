package storage;

import database.Storage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StorageTests {
    /**
     * Тест для проверки создания новой группы и подписки нового юзера на него
     */
    @Test
    public void testCheakAddtoStorageUser() {
        Storage dataBase1 = Storage.storageGetInstance();
        dataBase1.getGroupsBase()
                 .clear();
        dataBase1.addInfoToGroup("123", "123");
        assertEquals(1, dataBase1.getGroupsBase()
                                 .get("123")
                                 .size());
    }

    /**
     * Тест для добавления одинаковых юзеров к одной и той же группе
     */
    @Test
    public void testCheckAddIdenticalUsers() {
        Storage dataBase = Storage.storageGetInstance();
        dataBase.getGroupsBase()
                 .get("123")
                 .clear();
        dataBase.addInfoToGroup("123", "123");
        dataBase.addInfoToGroup("123", "123");
        assertEquals(1, dataBase.getGroupsBase()
                                 .get("123")
                                 .size());
    }

    /**
     * Тест для проверки, подключается ли пользователи к одной и той же группе или нет
     * Используется класс Storage
     */
    @Test
    public void testCheckAddVariousUsers() {
        Storage dataBase = Storage.storageGetInstance();
        dataBase.getGroupsBase()
                .clear();
        dataBase.addInfoToGroup("12", "1235");
        dataBase.addInfoToGroup("12", "1234");
        assertEquals(2, dataBase.getGroupsBase()
                                 .get("12")
                                 .size());
    }

    /**
     * Совокупность предыдущих двух тестов для проверки работы добавления одинаковых юзеров к одной группе
     * добавление одинаковых юзеров к разным группам
     * проверяем сумму подписок разных пользователей
     */
    @Test
    public void testForAddVariousAndIdenticalUsers() {
        Storage dataBase = Storage.storageGetInstance();
        dataBase.getGroupsBase()
                 .clear();
        dataBase.addInfoToGroup("12", "1235");
        dataBase.addInfoToGroup("12", "1234");
        dataBase.addInfoToGroup("12", "1234");
        dataBase.addInfoToGroup("123", "1234");
        dataBase.addInfoToGroup("123", "1234");
        assertEquals(3, dataBase.getGroupsBase()
                                 .get("12")
                                 .size() + dataBase.getGroupsBase()
                                                    .get("123")
                                                    .size());
    }
}
