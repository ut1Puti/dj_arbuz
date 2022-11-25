package dj.arbuz.database.local;

import com.google.gson.reflect.TypeToken;
import dj.arbuz.database.GroupBase;
import loaders.gson.GsonLoader;

import java.lang.reflect.Type;

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
public final class GroupsStorage implements GroupBase {
    /**
     * Поле хеш таблицы, где ключ - имя группы в социальной сети, значение - список пользователей
     */
    private Map<String, GroupRelatedData> groupsBase;

    /**
     * Поле блокировки обновления даты последнего поста
     */
    private final Object dataLock = new Object();

    private static GroupsStorage groupsStorage;

    /**
     * Метод для создания нового пользователя в наш класс
     *
     * @param userSubscribedToGroupId id пользователя
     * @param groupScreenName         короткое имя группы
     */
    private void addNewGroup(String groupScreenName, String userSubscribedToGroupId) {
        List<String> newList = new ArrayList<>();
        newList.add(userSubscribedToGroupId);
        groupsBase.put(groupScreenName, new GroupRelatedData(newList, Instant.now().getEpochSecond()));
    }

    /**
     * Метод для добавления информации для имеющегося в базе группы
     *
     * @param groupScreenName         короткое имя группы
     * @param userSubscribedToGroupId id пользователя
     */
    private boolean addOldGroup(String groupScreenName, String userSubscribedToGroupId) {
        if (!groupsBase.get(groupScreenName).contains(userSubscribedToGroupId)) {
            groupsBase.get(groupScreenName).addNewSubscriber(userSubscribedToGroupId);
            return true;
        }
        return false;
    }

    /**
     * метод для добавления информации, где происходит ветвление на методы добавления старой/новой группы
     *
     * @param groupScreenName         короткое имя группы
     * @param userSubscribedToGroupId id пользователя
     * @see GroupsStorage#addNewGroup(String, String)
     * @see GroupsStorage#addOldGroup(String, String)
     */
    public boolean addSubscriber(String groupScreenName, String userSubscribedToGroupId) {
        if (groupsBase.get(groupScreenName) == null) {
            addNewGroup(groupScreenName, userSubscribedToGroupId);
            return true;
        } else {
            return addOldGroup(groupScreenName, userSubscribedToGroupId);
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
        Type groupStorageMapType = new TypeToken<Map<String, GroupRelatedData>>() {
        }.getType();
        GsonLoader<Map<String, GroupRelatedData>> groupStorageMapGsonLoader = new GsonLoader<>(groupStorageMapType);
        try {
            groupStorageMapGsonLoader.loadToJson(LocalDatabasePaths.LOCAL_GROUP_DATA_BASE_PATH, groupsBase);
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
        Type groupStorageMapType = new TypeToken<Map<String, GroupRelatedData>>() {
        }.getType();
        GsonLoader<Map<String, GroupRelatedData>> loader = new GsonLoader<>(groupStorageMapType);
        try {
            groupsBase = loader.loadFromJson(LocalDatabasePaths.LOCAL_GROUP_DATA_BASE_PATH);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для удаления пользователя из подписчика группы
     *
     * @param groupScreenName         название группы в базе данных
     * @param userSubscribedToGroupId id пользователя
     * @return {@code true} если пользователь был удален, {@code false} если пользователь не был удален
     */
    public boolean deleteSubscriber(String groupScreenName, String userSubscribedToGroupId) {

        if (!groupsBase.containsKey(groupScreenName)) {
            return false;
        }

        List<String> subscribedToGroupUsers = groupsBase.get(groupScreenName).getSubscribedUsersId();

        if (!subscribedToGroupUsers.contains(userSubscribedToGroupId)) {
            return false;
        }

        boolean isUnsubscribed = subscribedToGroupUsers.remove(userSubscribedToGroupId);

        if (isUnsubscribed && subscribedToGroupUsers.isEmpty()) {
            groupsBase.remove(groupScreenName);
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
        synchronized (dataLock) {

            if (!groupsBase.containsKey(groupScreenName)) {
                return Optional.empty();
            }

            return Optional.of(groupsBase.get(groupScreenName).getLastPostDate());
        }
    }

    /**
     * Метод обновляющий дату последнего поста для группы по названию
     *
     * @param groupScreenName - название группы
     * @param newLastPostDate - новая дата последнего поста для группы
     */
    public void updateGroupLastPost(String groupScreenName, long newLastPostDate) {
        synchronized (dataLock) {

            if (!groupsBase.containsKey(groupScreenName)) {
                return;
            }

            if (newLastPostDate <= groupsBase.get(groupScreenName).getLastPostDate()) {
                return;
            }

            GroupRelatedData newGroupData = groupsBase.get(groupScreenName).updateLastPostDate(newLastPostDate);
            groupsBase.put(groupScreenName, newGroupData);
        }
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

    /**
     * Метод очищающий хранилище подписок и сохраняющий его в файл
     */
    public void clear() {
        saveToJsonFile();
        groupsBase.clear();
    }
}
