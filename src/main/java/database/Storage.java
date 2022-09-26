package database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.invoke.SwitchPoint;
import java.util.*;

/**
 * Класс для хранения подписок всех пользователей
 *
 * @author Щёголев Андрей
 * @version 0.1
 */
public class Storage {
    /**
     * Поле хеш таблицы где ключ - айди группы, значение - список пользователей
     */
    private Map<String, List<String>> map;

    /**
     * Метод для создания нового пользователя в наш класс
     *
     * @param userId  -айди пользователя
     * @param groupId - айди группы
     */
    private void addNewGroup(String groupId, String userId) {
        List<String> newList = new LinkedList<>();
        newList.add(userId);
        map.put(groupId, newList);
    }

    /**
     * Метод для добавления информации для имеющегося в базе группы
     *
     * @param groupId - Айди группы
     * @param userId
     */
    private void addOldGroup(String groupId, String userId) {
        List<String> updatedList = map.get(groupId);
        updatedList.add(userId);
        map.put(groupId, updatedList);
    }

    /**
     * метод для добавления информации  где происходит ветвление на методы добавления старой/новой группы
     * @param groupId - айди группы
     * @param userID - айди пользователя
     * @see Storage#addNewGroup(String, String) 
     * @see Storage#addOldGroup(String, String)
     */
    public void addInfoToGroup(String groupId, String userID) {
        if (map.get(groupId) == null) {
            addNewGroup(groupId, userID);
        } else {
            addOldGroup(groupId, userID);
        }
    }

    /**
     * Конструктор создания класса
     */
    public Storage() {
        map = new HashMap<>();
    }

    /**
     * Метод для сохранения хеш таблицы в виде файла с расширением json
     */
    public void saveToJsonFile() {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        try {
            FileWriter file = new FileWriter("src/main/resources/Users_database.json");
            file.write(json);
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для возврата хеш таблицы с помощью созданнового json файла
     * @see Storage#saveToJsonFile()
     *
     */
    public void returnStorageFromDatabase() {
        try {
            FileReader file = new FileReader("src/main/resources/Users_database.json");
            Gson jsonFile = new Gson();
            Scanner scanner = new Scanner(file);
            try {
                String json = scanner.nextLine();
                map = jsonFile.fromJson(json, new TypeToken<Map<String, List<String>>>() {
                }.getType());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

