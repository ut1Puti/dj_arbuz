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
    /**
     * Поле хранящее новые посты
     */
    private final ArrayBlockingQueue<List<String>> newPosts = new ArrayBlockingQueue<>(10);
    /**
     * Поле хранилища групп
     */
    private final Storage storage = Storage.getInstance();
    /**
     * Поле обработчика обращений к vk api
     */
    private Vk vk = new Vk("src/main/resources/anonsrc/vkconfig.properties");

    /**
     * Метод логики выполняемой внутри потока
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Map<String, List<String>> map = storage.getGroupsBase();
            Set<String> set = map.keySet();
            try {
                for (String key : set) {
                    Optional<List<String>> optional = vk.getNewPosts(key, 0);

                    if (optional.isPresent()) {
                        synchronized (newPosts) {
                            newPosts.put(optional.get());
                        }
                    }

                }
                final int oneMinuteInMilliseconds = 60000;
                Thread.sleep(oneMinuteInMilliseconds);
            } catch (NoGroupException ignored) {
            } catch (InterruptedException e) {
                break;
            } catch (ApiException | ClientException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @return true - если есть новые посты
     * false - если нет новых постов
     */
    public boolean hasNewPosts() {
        return !newPosts.isEmpty();
    }

    /**
     * Метод получающий новые посты
     *
     * @return список новых постов
     */
    public List<List<String>> getNewPosts() {
        List<List<String>> result;
        synchronized (newPosts) {
            result = newPosts.stream().toList();
            newPosts.clear();
        }
        return result;
    }

}
