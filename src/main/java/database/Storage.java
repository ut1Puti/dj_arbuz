package database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.*;
import java.util.*;

/**
 * Класс для хранения подписок всех пользователей
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
public class Storage {
    /**
     * Поле хеш таблицы, где ключ - айди группы, значение - список пользователей
     */
    private Map<String, List<String>> groupsBase;
    private static Storage storage = null;

    /**
     * Метод для создания нового пользователя в наш класс
     *
     * @param userId  -айди пользователя
     * @param groupId - айди группы
     */
    private void addNewGroup(String groupId, String userId) {
        List<String> newList = new LinkedList<>();
        newList.add(userId);
        groupsBase.put(groupId, newList);
    }

    /**
     * Метод для добавления информации для имеющегося в базе группы
     *
     * @param groupId - Айди группы
     * @param userId  - Айди пользователя
     */
    private boolean addOldGroup(String groupId, String userId) {
        if (!groupsBase.get(groupId).contains(userId)) {
            groupsBase.get(groupId)
                      .add(userId);
            return true;
        }
        return false;
    }

    /**
     * метод для добавления информации где происходит ветвление на методы добавления старой/новой группы
     *
     * @param groupId - айди группы
     * @param userID  - айди пользователя
     * @see Storage#addNewGroup(String, String)
     * @see Storage#addOldGroup(String, String)
     */
    public boolean addInfoToGroup(String groupId, String userID) {
        if (groupsBase.get(groupId) == null) {
            addNewGroup(groupId, userID);
            return true;
        } else {
            return addOldGroup(groupId, userID);
        }
    }

    /**
     * Конструктор создания класса
     */
    private Storage() {
        groupsBase = new HashMap<>();
        returnStorageFromDatabase();
    }

    public static Storage getInstance() {
        if (storage == null) {
            storage = new Storage();
        }
        return storage;
    }

    /**
     * Метод для сохранения хеш таблицы в виде файла с расширением json
     */
    public void saveToJsonFile() {
        Gson gson = new Gson();
        String json = gson.toJson(groupsBase);
        try {
            FileWriter file = new FileWriter("src/main/resources/anonsrc/Users_database.json");
            file.write(json);
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для возврата хеш таблицы с помощью созданнового json файла
     *
     * @see Storage#saveToJsonFile()
     */
    public void returnStorageFromDatabase() {
        try {
            FileReader file = new FileReader("src/main/resources/anonsrc/Users_database.json");
            Scanner scanner = new Scanner(file);
            try {
                String json = scanner.nextLine();
                Gson jsonFile = new Gson();
                groupsBase = jsonFile.fromJson(json, new TypeToken<Map<String, List<String>>>() {
                }.getType());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, List<String>> getGroupsBase() {
        return groupsBase;
    }
}
