package handlers.notifcations;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import database.Storage;
import handlers.vk.api.groups.NoGroupException;
import handlers.vk.api.VkApiHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class Notifications extends Thread {
    private ArrayBlockingQueue<List<String>> newPosts = new ArrayBlockingQueue<>(10);
    private Object lock = new Object();
    private static Storage storage = Storage.getInstance();
    private VkApiHandler vk = new VkApiHandler("src/main/resources/anonsrc/vkconfig.properties");
    private final int oneMinute = 60000;

    public Notifications() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            Map<String, List<String>> map = storage.getGroupsBase();
            Set<String> set = map.keySet();
            for (String key : set) {
                try {
                    Optional<List<String>> optional = vk.getNewPosts(key, 0);

                    if (optional.isPresent()) {
                        synchronized (lock) {
                            newPosts.put(optional.get());
                        }
                    }

                } catch (ApiException | ClientException | InterruptedException | NoGroupException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(oneMinute);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean haveNewPosts() {
        return !newPosts.isEmpty();
    }

    public List<List<String>> getNewPosts() {
        List<List<String>> result;
        synchronized (lock) {
            result = newPosts.stream().toList();
            newPosts.clear();
        }
        return result;
    }

    public void _stop() {
        this.interrupt();
    }
}
