package database;

import user.User;

import java.util.List;
import java.util.Map;

public class UserStorage implements Storage{
    private Map<String, User> groupsBase;
    private static Storage userStorage = null;
    @Override
    public boolean addInfoToGroup(String groupId, String userID) {
        return false;
    }

    @Override
    public void saveToJsonFile() {

    }

    @Override
    public void returnStorageFromDatabase() {

    }

    @Override
    public Map getBase() {
        return null;
    }
    public static Storage storageGetInstance() {
        if (userStorage == null) {
            userStorage = new UserStorage();
        }
        return userStorage;
    }
}
