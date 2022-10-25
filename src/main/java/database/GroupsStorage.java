package database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для хранения подписок всех пользователей
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
public class GroupsStorage {
    /**
     * Поле хеш таблицы, где ключ - имя группы в социальной сети, значение - список пользователей
     */
    private Map<String, GroupRelatedData> groupsBase;
    private static GroupsStorage groupsStorage = null;

    /**
     * Метод для создания нового пользователя в наш класс
     *
     * @param userId  id пользователя
     * @param groupId имя группы
     */
    private void addNewGroup(String groupId, String userId) {
        List<String> newList = new ArrayList<>();
        newList.add(userId);
        groupsBase.put(groupId, new GroupRelatedData(newList, Instant.now().getEpochSecond()));
    }

    /**
     * Метод для добавления информации для имеющегося в базе группы
     *
     * @param groupId - Айди группы
     * @param userId  - Айди пользователя
     */
    private boolean addOldGroup(String groupId, String userId) {
        if (!groupsBase.get(groupId).contains(userId)) {
            groupsBase.get(groupId).addNewSubscriber(userId);
            return true;
        }
        return false;
    }

    /**
     * метод для добавления информации, где происходит ветвление на методы добавления старой/новой группы
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
            FileWriter file = new FileWriter("src/main/resources/anonsrc/database_for_groups.json");
            file.write(json);
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для возврата хеш таблицы с помощью созданного json файла
     *
     * @see GroupsStorage#saveToJsonFile()
     */
    public void returnStorageFromDatabase() {
        try {
            FileReader file = new FileReader("src/main/resources/anonsrc/database_for_groups.json");
            Scanner scanner = new Scanner(file);
            try {
                String json = scanner.nextLine();
                Gson jsonFile = new Gson();
                groupsBase = jsonFile.fromJson(json, new TypeToken<Map<String, GroupRelatedData>>() {
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
     * Метод для удаления пользователя из подписчика группы
     *
     * @param groupName название группы в базе данных
     * @param userId    id пользователя
     * @return {@code true} если пользователь был удален, {@code false} если пользователь не был удален
     */
    public boolean deleteInfoFromGroup(String groupName, String userId) {

        if (!groupsBase.containsKey(groupName)) {
            return false;
        }

        List<String> subscribedToGroupUsers = groupsBase.get(groupName).getSubscribedUsersId();

        if (!subscribedToGroupUsers.contains(userId)) {
            return false;
        }

        boolean isUnsubscribed = subscribedToGroupUsers.remove(userId);

        if (isUnsubscribed && subscribedToGroupUsers.isEmpty()) {
            groupsBase.remove(groupName);
        }

        return isUnsubscribed;
    }

    /**
     * Метод получающий все группы на которые оформлены подписки
     *
     * @return неизменяемый набор коротких названий групп на которые оформлены подписки
     */
    public Set<String> getGroups() {
        return Set.copyOf(groupsBase.keySet());
    }

    /**
     * Метод получающий всех подписчиков определенной группы
     *
     * @param groupScreenName - короткое название группы
     * @return неизменяемый список подписчиков группы
     */
    public List<String> getSubscribedToGroupUsersId(String groupScreenName) {
        return List.copyOf(groupsBase.get(groupScreenName).getSubscribedUsersId());
    }

    /**
     * Метод получающий подписки пользователя
     *
     * @param userId id пользователя, подписки которого нужно получить
     * @return подписки пользователя
     */
    public Set<String> getUserSubscribedGroups(String userId) {
        return groupsBase.entrySet().stream()
                .filter(groupNameAndInformation -> groupNameAndInformation.getValue().contains(userId))
                .map(Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Метод получающий дату последнего поста для группы по названию
     *
     * @param groupScreenName - название группы
     * @return дату последнего поста
     */
    public Optional<Long> getGroupLastPostDate(String groupScreenName) {

        if (!groupsBase.containsKey(groupScreenName)) {
            return Optional.empty();
        }

        return Optional.of(groupsBase.get(groupScreenName).getLastPostDate());
    }

    /**
     * Метод обновляющий дату последнего поста для группы по названию
     *
     * @param groupScreenName - название группы
     * @param newLastPostDate - новая дата последнего поста для группы
     */
    public void updateGroupLastPost(String groupScreenName, long newLastPostDate) {

        if (!groupsBase.containsKey(groupScreenName)) {
            return;
        }

        groupsBase.get(groupScreenName).updateLastPostDate(newLastPostDate);
    }

    /**
     * Метод проверяющий есть группа в базе данных
     *
     * @param groupScreenName - имя группы
     * @return true - если группа есть
     * false - если группы нет
     */
    public boolean containsGroup(String groupScreenName) {
        return groupsBase.containsKey(groupScreenName);
    }
}
