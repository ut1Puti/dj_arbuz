package dj.arbuz.database.hibernate.UsersDao;


import com.google.common.reflect.TypeToken;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.hibernate.entity.GroupData;
import loaders.gson.GsonLoader;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для взаимодействия с репозиторием для групп и изменением данных в Entity классе
 */
public class GroupDatabase implements GroupBase {
    /** Репозиторий группы**/
    final private GroupDao groupDao = new GroupDao();

    /**
     * Метод для добавления подписки к группе
     * @param groupScreenName Айди группы
     * @param userSubscribedToGroupId Айди пользователя
     * @return добавляется новая информация в Entity класс или же создается новый Entity класс
     */
    public boolean addInfoToGroup(String groupScreenName, String userSubscribedToGroupId) {

        if (containsGroup(groupScreenName)) {
            return addOldGroup(groupScreenName, userSubscribedToGroupId);
        }

        return addNewGroup(groupScreenName, userSubscribedToGroupId);
    }

    /**
     * Метод для получения всех подписчиков по определенной группе
     * @param groupData Entity класс групы
     * @return список пользователей в которых хранятся их айди в телеграме
     */
    private List<String> getSubscribedGroup(GroupData groupData) {
        GsonLoader<List<String>> gson = new GsonLoader<>(new TypeToken<List<String>>() {
        }.getType());
        return gson.createFromJsonString(groupData.getUsers());
    }

    /**
     *  Метод для сохранения юзеров в виде JSON строки
     * @param groupData Список Entity классов групп
     * @return строка вида JSON файла
     */
    private String saveSubscribers(List<String> groupData) {
        GsonLoader<List<String>> gson = new GsonLoader<>(new TypeToken<List<String>>() {
        }.getType());
        return gson.createToJsonString(groupData);
    }

    /**
     * Мето для добавления новой группы
     * @param groupId Айди группы
     * @param userId Айди юзера
     * @return логическое выражение, сохранилось или нет
     */
    private boolean addNewGroup(String groupId, String userId) {
        GroupData groupData = new GroupData();
        groupData.setGroupName(groupId);
        groupData.setDateLastPost(Instant.now().getEpochSecond());
        groupData.setUsers(saveSubscribers(List.of(userId)));
        return groupDao.saveGroup(groupData);
    }

    /**
     * Метод для добавления информации для имеющегося в базе группы
     *
     * @param groupId - Айди группы
     * @param userId  - Айди пользователя
     */
    private boolean addOldGroup(String groupId, String userId) {
        GroupData groupData = groupDao.getGroup(groupId);

        if (groupData == null) {
            return false;
        }

        List<String> users = getSubscribedGroup(groupData);

        if (!users.contains(userId)) {
            users.add(userId);
            groupData.setUsers(saveSubscribers(users));
            return groupDao.updateGroup(groupData);
        }

        return false;
    }

    /**
     * Мето для удаления подписчика определенной группы
     * @param groupScreenName Айди группы
     * @param userSubscribedToGroupId Айди юзера
     */
    public boolean deleteInfoFromGroup(String groupScreenName, String userSubscribedToGroupId) {
        GroupData groupData = groupDao.getGroup(groupScreenName);

        if (groupData == null) {
            return false;
        }

        List<String> subscribedToGroupUsers = getSubscribedGroup(groupData);

        boolean isUnsubscribed = subscribedToGroupUsers.remove(userSubscribedToGroupId);
        if (isUnsubscribed && subscribedToGroupUsers.isEmpty()) {
            groupDao.deleteGroup(groupData.getGroupName());
        } else {
            groupData.setUsers(saveSubscribers(subscribedToGroupUsers));
            groupDao.saveGroup(groupData);
        }

        return isUnsubscribed;
    }

    /**
     * Метод для поиска всех подписчков группы
     * @param groupScreenName айди группы
     */
    public List<String> getSubscribedToGroupUsersId(String groupScreenName) {
        GroupData groupData = groupDao.getGroup(groupScreenName);

        if (groupData == null) {
            return List.of();
        }

        return getSubscribedGroup(groupData);
    }

    /**
     * Метод получения даты последнего поста
     * @param groupScreenName айди группы
     */
    public Optional<Long> getGroupLastPostDate(String groupScreenName) {
        GroupData groupData = groupDao.getGroup(groupScreenName);
        if (groupData == null) {
            return Optional.empty();
        }

        return Optional.of(groupData.getDateLastPost());
    }

    /**
     * Мето для обновления даты последнего поста
     * @param groupScreenName айди группы
     * @param newLastPostDate новая дата последнего поста
     */
    public void updateGroupLastPost(String groupScreenName, long newLastPostDate) {
        GroupData groupData = groupDao.getGroup(groupScreenName);

        if (groupData == null) {
            return;
        }

        groupData.setDateLastPost(newLastPostDate);
        groupDao.updateGroup(groupData);
    }

    /**
     * Метод для проверки есть ли такая группа в базе данных
     * @param groupScreenName айди группы
     */
    @Override
    public boolean containsGroup(String groupScreenName) {
        return groupDao.getGroup(groupScreenName) != null;
    }

    /**
     * Метод для получения всех групп сразу
     */
    public Set<String> getGroups() {
        return groupDao.getAllGroups()
                       .stream()
                       .filter(Objects::nonNull)
                       .map(GroupData::getGroupName)
                       .collect(Collectors.toSet());
    }

    /**
     * Получение всех групп на которые подписан пользователь
     * @param userId айди юзера
     */
    public Set<String> getUserSubscribedGroups(String userId) {
        return groupDao.getAllGroups()
                       .stream()
                       .filter(Objects::nonNull)
                       .filter(v -> getSubscribedGroup(v).contains(userId))
                       .map(GroupData::getGroupName)
                       .collect(Collectors.toSet());
    }
}
