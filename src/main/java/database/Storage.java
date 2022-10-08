package database;

import java.util.List;
import java.util.Map;

public interface Storage {
    public boolean addInfoToGroup(String groupId, String userID);

    public void saveToJsonFile();
    public void returnStorageFromDatabase();
    public Map getBase();
}
