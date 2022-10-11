package storage;

import database.GroupsStorage;
import org.junit.jupiter.api.Test;

public class StorageTests {
    /**
     * Тест для проверки создания новой группы и подписки нового юзера на него
     */
    @Test
    public void testCheakAddtoStorageUser() {
<<<<<<< HEAD
        Storage dataBase1 = Storage.getInstance();
        dataBase1.getGroupsBase()
=======
        GroupsStorage dataBase1 = GroupsStorage.getInstance();
        dataBase1.getBase()
>>>>>>> 93871f73411302f1ca81c3d7ae2451db1783b69f
                 .clear();
        dataBase1.addInfoToGroup("123", "123");
        assertEquals(1, dataBase1.getBase()
                                 .get("123")
                                 .size());
    }

    /**
     * Тест для добавления одинаковых юзеров к одной и той же группе
     */
    @Test
    public void testCheckAddIdenticalUsers() {
<<<<<<< HEAD
        Storage dataBase = Storage.getInstance();
        dataBase.getGroupsBase()
=======
        GroupsStorage dataBase = GroupsStorage.getInstance();
        dataBase.getBase()
>>>>>>> 93871f73411302f1ca81c3d7ae2451db1783b69f
                 .get("123")
                 .clear();
        dataBase.addInfoToGroup("123", "123");
        dataBase.addInfoToGroup("123", "123");
        assertEquals(1, dataBase.getBase()
                                 .get("123")
                                 .size());
    }

    /**
     * Тест для проверки, подключается ли пользователи к одной и той же группе или нет
     * Используется класс Storage
     */
    @Test
    public void testCheckAddVariousUsers() {
<<<<<<< HEAD
        Storage dataBase = Storage.getInstance();
        dataBase.getGroupsBase()
=======
        GroupsStorage dataBase = GroupsStorage.getInstance();
        dataBase.getBase()
>>>>>>> 93871f73411302f1ca81c3d7ae2451db1783b69f
                .clear();
        dataBase.addInfoToGroup("12", "1235");
        dataBase.addInfoToGroup("12", "1234");
        assertEquals(2, dataBase.getBase()
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
<<<<<<< HEAD
        Storage dataBase = Storage.getInstance();
        dataBase.getGroupsBase()
=======
        GroupsStorage dataBase = GroupsStorage.getInstance();
        dataBase.getBase()
>>>>>>> 93871f73411302f1ca81c3d7ae2451db1783b69f
                 .clear();
        dataBase.addInfoToGroup("12", "1235");
        dataBase.addInfoToGroup("12", "1234");
        dataBase.addInfoToGroup("12", "1234");
        dataBase.addInfoToGroup("123", "1234");
        dataBase.addInfoToGroup("123", "1234");
        assertEquals(3, dataBase.getBase()
                                 .get("12")
                                 .size() + dataBase.getBase()
                                                    .get("123")
                                                    .size());
    }
}
