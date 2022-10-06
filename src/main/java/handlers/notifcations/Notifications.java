package handlers.notifcations;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Notifications {
    private ArrayBlockingQueue<List<String>> post = new ArrayBlockingQueue<>(10);
    private NotificationsPullingThread pullingThread = new NotificationsPullingThread(post);

    public Notifications() {
        //pullingThread.start();
    }

    public boolean haveNew() {
        return !post.isEmpty();
    }

    public List<List<String>> getNew() {
        List<List<String>> result = post.stream().toList();
        post.clear();
        return result;
    }

    public void stop() {
        pullingThread.interrupt();
    }
}
