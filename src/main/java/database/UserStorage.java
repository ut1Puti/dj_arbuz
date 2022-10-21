package database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import user.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UserStorage{
    private Map<String, User> usersBase;
    private static UserStorage userStorage = null;

    public boolean addInfoUser(String a, User b) {
        usersBase.put(a, b);
        return true;
    }

    public void saveToJsonFile() {
        Gson gson = new Gson();
        String json = gson.toJson(usersBase);
        try {
            FileWriter file = new FileWriter("src/main/resources/anonsrc/database_for_users.json");
            file.write(json);
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void returnStorageFromDatabase() {
        try {
            FileReader file = new FileReader("src/main/resources/anonsrc/database_for_users.json");
            Scanner scanner = new Scanner(file);
            try {
                String json = scanner.nextLine();
                Gson jsonFile = new Gson();
                usersBase = jsonFile.fromJson(json, new TypeToken<Map<String, User>>() {
                }.getType());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static UserStorage getInstance() {
        if (userStorage == null) {
            userStorage = new UserStorage();
        }
        return userStorage;
    }

    private UserStorage() {
        usersBase = new HashMap<>();
        returnStorageFromDatabase();
    }

    public boolean contains(String a) {
        return usersBase.containsKey(a);
    }

    public User getUser(String a) {
        return usersBase.get(a);
    }
}
