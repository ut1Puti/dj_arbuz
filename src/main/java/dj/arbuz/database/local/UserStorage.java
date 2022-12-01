<<<<<<<< HEAD:console/src/main/java/dj/arbuz/database/UserStorage.java
package dj.arbuz.database;
========
package dj.arbuz.database.local;
>>>>>>>> developTaskFour:src/main/java/dj/arbuz/database/local/UserStorage.java

import com.google.gson.reflect.TypeToken;
import dj.arbuz.database.UserBase;
import loaders.gson.GsonLoader;
import dj.arbuz.user.BotUser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public final class UserStorage implements UserBase {
    private Map<String, BotUser> usersBase;

    private static UserStorage userStorage = null;

    /**
     * Метод для добавления информации в базу данных
     *
     * @param userId  id юзера в телеграмме
     * @param botUser данные юзера
     * @return {@code true}
     */
    public boolean addUser(String userId, BotUser botUser) {
        usersBase.put(userId, botUser);
        return true;
    }

    /**
     * Метод для сохранения базы данных в json файл
     */
    public void saveToJsonFile() {
        Type userStorageMapType = new TypeToken<Map<String, BotUser>>() {
        }.getType();
        GsonLoader<Map<String, BotUser>> loader = new GsonLoader<>(userStorageMapType);
        try {
            loader.loadToJson(LocalDatabasePaths.LOCAL_USER_DATA_BASE_PATH, usersBase);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для получения информации с json файла
     */
    public void returnStorageFromDatabase() {
        Type userStorageMapType = new TypeToken<Map<String, BotUser>>() {
        }.getType();
        GsonLoader<Map<String, BotUser>> loader = new GsonLoader<>(userStorageMapType);
        try {
            usersBase = loader.loadFromJson(LocalDatabasePaths.LOCAL_USER_DATA_BASE_PATH);
        } catch (IOException e) {
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
    public boolean contains(String userId) {
        return usersBase.containsKey(userId);
    }

    /**
     * Метод для оплучения значения по ключу
     *
     * @param userId ключ с id юзера в телеграмме
     * @return подписки на группы
     */
    public BotUser getUser(String userId) {
        return usersBase.get(userId);
    }

    /**
     * Метод получающий всех пользователей пользующихся консольной версией бота
     *
     * @return список id пользователей пользующихся консольной версией бота
     */
    @Override
    public List<String> getAllUsersId() {
        return usersBase.keySet().stream().toList();
    }

    /**
     * Метод проверяющий является ли пользователь админом приложения
     *
     * @param userId id пользователя
     * @return {@code true} тк пользователь консольной версии является единственным пользователем приложения
     */
    @Override
    public boolean isAdmin(String userId) {
        return true;
    }

    /**
     * Метод удаляющий пользователя из базы данных
     *
     * @param userTelegramId id пользователя в телеграме
     */
    @Override
    public void deleteUser(String userTelegramId) {
        usersBase.remove(userTelegramId);
    }
}
