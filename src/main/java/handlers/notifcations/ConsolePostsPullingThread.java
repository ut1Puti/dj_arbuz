package handlers.notifcations;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.vk.groups.NoGroupException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Класс получающий обновления постов в группах для консольного пользователя
 *
 * @author Кедровских Олег
 * @version 0.5
 */
public class ConsolePostsPullingThread extends AbstractPostsPullingThread {
    /**
     * Поле хранящее новые посты
     */
    private final ArrayBlockingQueue<List<String>> newPosts = new ArrayBlockingQueue<>(10);

    /**
     * Метод логики выполняемой внутри потока
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Map<String, List<String>> map = storage.getBase();
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
    @Override
    public boolean hasNewPosts() {
        return !newPosts.isEmpty();
    }

    /**
     * Метод получающий новые посты
     *
     * @return список новых постов
     */
    @Override
    public List<List<String>> getNewPosts() {
        List<List<String>> result;
        synchronized (newPosts) {
            result = newPosts.stream().toList();
            newPosts.clear();
        }
        return result;
    }
}
