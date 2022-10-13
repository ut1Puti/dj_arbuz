package dj.arbuz.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Класс для хранения подписок всех пользователей
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
public class GroupsStorage {
    /**
     * Поле хеш таблицы, где ключ - айди группы, значение - список пользователей
     */
    private Map<String, List<String>> groupsBase;
    private static GroupsStorage groupsStorage = null;

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
     * @see GroupsStorage#addNewGroup(String, String)
     * @see GroupsStorage#addOldGroup(String, String)
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
    private GroupsStorage() {
        groupsBase = new HashMap<>();
        returnStorageFromDatabase();
    }

    public static GroupsStorage getInstance() {
        if (groupsStorage == null) {
            groupsStorage = new GroupsStorage();
        }
        return groupsStorage;
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
     * @see GroupsStorage#saveToJsonFile()
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

    /**
     * Метод получающий все группы на которые оформлены подписки
     *
     * @return группы на которые оформлены подписки
     */
    public List<String> getGroups() {
        return groupsBase.keySet().stream().toList();
    }

    /**
     * Метод получающий всех подписчиков определенной группы
     *
     * @param groupScreenName - короткое название группы
     * @return подписчиков группы
     */
    public List<String> getSubscribedToGroupUsersId(String groupScreenName) {
        return groupsBase.get(groupScreenName);
    }

    /**
     * Метод получающий подписки пользователя
     *
     * @param userId - id пользователя, подписки которого нужно получить
     * @return подписки пользователя
     */
    public List<String> getUserSubscribedGroups(String userId) {
        List<String> userSubscribedGroups = new ArrayList<>();
        for (Entry<String, List<String>> groupNameAndSubscribers : groupsBase.entrySet()) {

            if (groupNameAndSubscribers.getValue().contains(userId)) {
                userSubscribedGroups.add(groupNameAndSubscribers.getKey());
            }

        }
        return userSubscribedGroups;
    }
}
