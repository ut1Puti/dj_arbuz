package handlers.notifcations;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.vk.groups.NoGroupException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Класс получающий обновления постов в группах для консольного пользователя
 *
 * @author Кедровских Олег
 * @version 0.5
 */
public class ConsolePostsPullingThread extends PostsPullingThread {
    /**
     * Поле хранящее новые посты
     */
    private final ArrayBlockingQueue<String> newPostsQueue = new ArrayBlockingQueue<>(10);
    /**
     * Поле id консольного пользователя
     */
    private final String consoleBotUserId;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleUserId - id консольного пользователя в database
     */
    public ConsolePostsPullingThread(String consoleUserId) {
        this.consoleBotUserId = consoleUserId;
    }

    /**
     * Метод логики выполняемой внутри потока
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                List<String> consoleUserSubscribedGroups = groupsBase.getUserSubscribedGroups(consoleBotUserId);
                for (String key : consoleUserSubscribedGroups) {
                    Optional<List<String>> threadFindNewPosts = vk.getNewPosts(key, 0);

                    if (threadFindNewPosts.isPresent()) {
                        for (String threadFindNewPost : threadFindNewPosts.get()) {
                            synchronized (newPostsQueue) {
                                newPostsQueue.put(threadFindNewPost);
                            }
                        }
                    }

                }
                final int oneHourInMilliseconds = 3600000;
                Thread.sleep(oneHourInMilliseconds);
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
        return !newPostsQueue.isEmpty();
    }

    /**
     * Метод получающий новые посты
     *
     * @return список новых постов
     */
    @Override
    public List<String> getNewPosts() {
        List<String> result;
        synchronized (newPostsQueue) {
            result = newPostsQueue.stream().toList();
            newPostsQueue.clear();
        }
        return result;
    }
}
