package handlers.notifcations;

import java.util.concurrent.ArrayBlockingQueue;

public class Notifications {
    private ArrayBlockingQueue<String> post = new ArrayBlockingQueue<>(10);
    private NotificationsPullingThread pullingThread = new NotificationsPullingThread(post);

    public Notifications() {

    }

}
