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

    /**
     * Метод для добавления информации в базу данных
     *
     * @param user - айди юзера в телеграмме
     * @param userData - данные юзера
     * @return
     */
    public boolean addInfoUser(String user, User userData) {
        usersBase.put(user, userData);
        return true;
    }
    
    /**
     * Метод для сохранения базы данных в json файл
     */
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

    /**
     * Метод для получения информации с json файла
     */
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

    /**
     * Инициализация бота
     *
     * @return Базу с юзерами
     */
    public static UserStorage getInstance() {
        if (userStorage == null) {
            userStorage = new UserStorage();
        }
        return userStorage;
    }

    /**
     * Метод для получения базы с юзерами
     */
    private UserStorage() {
        usersBase = new HashMap<>();
        returnStorageFromDatabase();
    }
    /**
     * Метод для проверки ключей в базе
     */
    public boolean contains(String user) {
        return usersBase.containsKey(user);
    }

    /**
     * Метод для оплучения значения по ключу
     *
     * @param user - ключ с айди юзера в телеграмме
     * @return подписки на группы
     */
    public User getUser(String user) {
        return usersBase.get(user);
    }
}
