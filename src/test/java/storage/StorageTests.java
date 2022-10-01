package storage;

import database.Storage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StorageTests {

    @Test
    public void TestFirst() {
        Storage dataBase1 = Storage.storageGetInstance();
        dataBase1.getGroupsBase()
                 .clear();
        dataBase1.addInfoToGroup("123", "123");
        assertEquals(1, dataBase1.getGroupsBase()
                                 .get("123")
                                 .size());
    }

    @Test
    public void TestSecond() {
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

    @Test
    public void TestThird() {
        Storage dataBase = Storage.storageGetInstance();
        dataBase.getGroupsBase()
                 .get("123")
                 .clear();
        dataBase.addInfoToGroup("12", "1235");
        dataBase.addInfoToGroup("12", "1234");
        assertEquals(2, dataBase.getGroupsBase()
                                 .get("12")
                                 .size());
    }

    @Test
    public void TestFourth() {
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
