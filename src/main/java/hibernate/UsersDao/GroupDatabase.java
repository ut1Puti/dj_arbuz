package hibernate.UsersDao;


import com.google.common.reflect.TypeToken;
import hibernate.entity.GroupData;
import hibernate.entity.UserData;
import loaders.gson.GsonLoader;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

;

public class GroupDatabase {

    final private GroupDao groupDao = new GroupDao();

    public boolean addInfoToGroup(String groupId, String userID) {
        if (contains(groupId))
            return addOldGroup(groupId, userID);
        return addNewGroup(groupId, userID);
    }

    private List<String> getSubscribedGroup(GroupData groupData) {
        GsonLoader<List<String>> gson = new GsonLoader<>(new TypeToken<List<String>>() {
        }.getType());
        return gson.createFromJsonString(groupData.getUsers());
    }

    private String saveSubscribers(List<String> groupData) {
        GsonLoader<List<String>> gson = new GsonLoader<>(new TypeToken<List<String>>() {
        }.getType());
        return gson.createToJsonString(groupData);
    }

    private boolean addNewGroup(String groupId, String userId) {
        GroupData groupData = new GroupData();
        groupData.setGroupName(groupId);
        groupData.setDateLastPost(Instant.now()
                                         .getEpochSecond());
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

    public boolean deleteInfoFromGroup(String groupName, String userId) {

        if (!contains(groupName)) {
            return false;
        }
        GroupData groupData = groupDao.getGroup(groupName);
        List<String> subscribedToGroupUsers = getSubscribedGroup(groupData);

        boolean isUnsubscribed = subscribedToGroupUsers.remove(userId);
        if (isUnsubscribed && subscribedToGroupUsers.isEmpty()) {
            groupDao.deleteGroup(groupData.getGroupName());
        }

        return isUnsubscribed;
    }

    public List<String> getSubscribedToGroupUsersId(String groupScreenName) {
        GroupData groupData = groupDao.getGroup(groupScreenName);
        if (groupData == null) {
            return List.of();
        }
        return getSubscribedGroup(groupData);
    }

    public Optional<Long> getGroupLastPostDate(String groupScreenName) {
        GroupData groupData = groupDao.getGroup(groupScreenName);
        if (groupData == null) {
            return Optional.empty();
        }

        return Optional.of(groupData.getDateLastPost());
    }

    public void updateGroupLastPost(String groupScreenName, long newLastPostDate) {
        GroupData groupData = groupDao.getGroup(groupScreenName);
        if (groupData == null) {
            return;
        }
        groupData.setDateLastPost(newLastPostDate);
        groupDao.updateGroup(groupData);
    }

    public Set<String> getGroups() {
        return groupDao.getAllGroups()
                       .stream()
                       .filter(Objects::nonNull)
                       .map(GroupData::getGroupName)
                       .collect(Collectors.toSet());
    }

    public Set<String> getUserSubscribedGroups(String userId) {

        return groupDao.getAllGroups()
                       .stream()
                       .filter(Objects::nonNull)
                       .filter(v -> getSubscribedGroup(v).contains(userId))
                       .map(GroupData::getGroupName)
                       .collect(Collectors.toSet());
    }

    private boolean contains(String group) {
        return groupDao.getGroup(group) != null;
    }
}
