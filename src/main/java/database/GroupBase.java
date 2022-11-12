package database;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GroupBase {
    boolean addInfoToGroup(String groupId, String userID);

    boolean deleteInfoFromGroup(String groupName, String userId);

    Set<String> getGroups();

    List<String> getSubscribedToGroupUsersId(String groupScreenName);

    Set<String> getUserSubscribedGroups(String userId);

    Optional<Long> getGroupLastPostDate(String groupScreenName);

    void updateGroupLastPost(String groupScreenName, long newLastPostDate);

    boolean containsGroup(String groupScreenName);
}
