package handlers.notifcations;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import database.Storage;
import handlers.vk.groups.NoGroupException;
import handlers.vk.Vk;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Класс получающий обновления постов в группах
 *
 * @author Кедровских Олег
 * @version 0.5
 */
public class NotificationsPullingThread extends Thread {
    private boolean working = false;
    /** Поле хранящее новые посты */
    private final ArrayBlockingQueue<List<String>> newPosts = new ArrayBlockingQueue<>(10);
    /** Поле синхолнизатора доступа */
    private final Object writeAndReadNewPostsLock = new Object();
    /** Поле хранилища групп */
    private final Storage storage = Storage.getInstance();
    /** Поле обработчика обращений к vk api */
    private Vk vk = new Vk("src/main/resources/anonsrc/vkconfig.properties");

    /**
     * Метод логики выполняемой внутри потока
     */
    @Override
    public void run() {
        working = true;
        while (working) {
            Map<String, List<String>> map = storage.getGroupsBase();
            Set<String> set = map.keySet();
            for (String key : set) {
                try {
                    Optional<List<String>> optional = vk.getNewPosts(key, 0);

                    if (optional.isPresent()) {
                        synchronized (writeAndReadNewPostsLock) {
                            newPosts.put(optional.get());
                        }
                    }

                } catch (NoGroupException ignored) {
                } catch (ApiException | ClientException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                final int oneMinute = 60000;
                Thread.sleep(oneMinute);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @return true - если есть новые посты
     *         false - если нет новых постов
     */
    public boolean haveNewPosts() {
        return !newPosts.isEmpty();
    }

    /**
     * Метод получающий новые посты
     *
     * @return список новых постов
     */
    public List<List<String>> getNewPosts() {
        List<List<String>> result;
        synchronized (writeAndReadNewPostsLock) {
            result = newPosts.stream().toList();
            newPosts.clear();
        }
        return result;
    }

    /**
     * Останавливает поток
     */
    public void _stop() {
        working = false;
    }
}
