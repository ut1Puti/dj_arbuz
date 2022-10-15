package database;

import java.util.List;

public class GroupRelatedData {
    private List<String> subscribedUsersId;
    private int lastPostDate;

    public GroupRelatedData() {

    }

    public void addSubscriber(String newSubscriberId) {
        subscribedUsersId.add(newSubscriberId);
    }
}
