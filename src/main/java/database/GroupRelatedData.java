package database;

import java.util.ArrayList;
import java.util.List;

public class GroupRelatedData {
    private List<String> subscribedUsersId = new ArrayList<>();
    private int lastPostDate = 0;

    public GroupRelatedData() {

    }

    public GroupRelatedData(List<String> subscribedUsersId) {
        this.subscribedUsersId = subscribedUsersId;
    }

    public List<String> getSubscribedUsersId() {
        return subscribedUsersId;
    }

    public int getLastPostDate() {
        return lastPostDate;
    }

    public void addSubscriber(String newSubscriberId) {
        subscribedUsersId.add(newSubscriberId);
    }

    public void addNewSubscribers(List<String> newSubscribersId) {
        subscribedUsersId.addAll(newSubscribersId);
    }

    public void updateLastPostDate(int newLastPostDate) {
        lastPostDate = newLastPostDate;
    }
}
