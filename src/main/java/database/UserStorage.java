package database;

import com.google.gson.reflect.TypeToken;
import loaders.gson.GsonLoader;
import user.BotUser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;

public class UserStorage implements UserBase {
    private Map<String, BotUser> usersBase;
    private static UserStorage userStorage = null;

    /**
     * Метод для добавления информации в базу данных
     *
     * @param user - айди юзера в телеграмме
     * @param userData - данные юзера
     * @return {@code true}
     */
    public boolean addInfoUser(String user, BotUser userData) {
        usersBase.put(user, userData);
        return true;
    }
    
    /**
     * Метод для сохранения базы данных в json файл
     */
    public void saveToJsonFile() {
        Type userStorageMapType = new TypeToken<Map<String, BotUser>>(){}.getType();
        GsonLoader<Map<String, BotUser>> loader = new GsonLoader<>(userStorageMapType);
        try {
            loader.loadToJson("src/main/resources/anonsrc/database_for_users.json", usersBase);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для получения информации с json файла
     */
    public void returnStorageFromDatabase() {
        Type userStorageMapType = new TypeToken<Map<String, BotUser>>(){}.getType();
        GsonLoader<Map<String, BotUser>> loader = new GsonLoader<>(userStorageMapType);
        try {
            usersBase = loader.loadFromJson("src/main/resources/anonsrc/database_for_users.json");
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
    public BotUser getUser(String user) {
        return usersBase.get(user);
    }
}
