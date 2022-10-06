package handlers.notifcations;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import database.Storage;
import handlers.vkapi.NoGroupException;
import handlers.vkapi.VkApiHandler;
import user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class NotificationsPullingThread extends Thread {
    private ArrayBlockingQueue<List<String>> arr;
    private static Storage storage = Storage.getInstance();
    private VkApiHandler vk = new VkApiHandler("src/main/resources/anonsrc/vkconfig.properties");

    public NotificationsPullingThread(ArrayBlockingQueue<List<String>> arr) {
        this.arr = arr;
    }

    @Override
    public void run() {
        while (true) {
            Map<String, List<String>> map = storage.getGroupsBase();
            Set<String> set = map.keySet();
            for (String key : set) {
                //TODO ENDLESS PULLING OF USER WITH VALID TOKEN
                try {
                    Optional<List<String>> optional = vk.getNewPosts(key, 0);

                    if (optional.isPresent()) {
                        arr.put(optional.get());
                    }

                } catch (ApiException | ClientException | InterruptedException | NoGroupException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
