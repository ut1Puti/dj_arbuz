package handlers.notifcations;

import database.Storage;
import user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class NotificationsPullingThread extends Thread {
    private ArrayBlockingQueue<String> arr;
    private static Storage storage = Storage.getInstance();

    public NotificationsPullingThread(ArrayBlockingQueue<String> arr) {
        this.arr = arr;
    }

    @Override
    public void run() {
        Map<String, List<User>> map = storage.getGroupsBase();

        try {
            Thread.sleep(3600000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
