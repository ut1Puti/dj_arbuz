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

/**
 * Класс получающий обновления постов в группах
 *
 * @author Кедровских Олег
 * @version 0.4
 */
public class NotificationsPullingThread extends Thread {
    /** Поле хранящее новые посты */
    private ArrayBlockingQueue<List<String>> newPosts = new ArrayBlockingQueue<>(10);
    /** Поле синхолнизатора доступа */
    private final Object lock = new Object();
    /** Поле хранилища групп */
    private Storage storage = Storage.getInstance();
    /** Поле обработчика обращений к vk api */
    private VkApiHandler vk = new VkApiHandler("src/main/resources/anonsrc/vkconfig.properties");
    /** Поле времени ожидания до следующего получения сообщений */
    private final int oneMinute = 60000;

    /**
     * Конструктор - создает экземпляр класса
     */
    public NotificationsPullingThread() {
        this.start();
    }

    /**
     * Метод логики выполняемой внутри потока
     */
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
                } catch (NoGroupException ignored) {
                } catch (ApiException | ClientException | InterruptedException e) {
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
        synchronized (lock) {
            result = newPosts.stream().toList();
            newPosts.clear();
        }
        return result;
    }

    /**
     * Останавливает поток
     */
    public void _stop() {
        this.interrupt();
    }
}
