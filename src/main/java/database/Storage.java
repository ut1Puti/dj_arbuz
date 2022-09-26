package database;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Storage {
    private Map<String, List<String>> map;

    private void addNewUser(String id, String group) {
        List<String> newList = new LinkedList<>();
        newList.add(group);
        map.put(id, newList);
    }

    private void addInfoToOldUser(String id, String group) {
        List<String> updatedList = map.get(id);
        updatedList.add(group);
        map.put(id, updatedList);
    }

    public void addInfoToUser(String groupID, String userID) {
        if (map.get(groupID) == null) {
            addNewUser(groupID, userID);
        } else {
            addInfoToOldUser(groupID, userID);
        }
    }

    public Storage() {

        map = new HashMap<String, List<String>>();
    }

    public List<String> getGroupInformation(String groupId) {
        return map.get(groupId);
    }

    public void saveToJsonFile() {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        System.out.println(json);
        try {
            FileOutputStream file = new FileOutputStream("Users_database.json");
            file.write(json.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Storage temp = new Storage();
        temp.addInfoToUser("Abc", "1");
        temp.addInfoToUser("Abc1", "2");
        temp.saveToJsonFile();
    }
}

