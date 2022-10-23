package handlers.notifcations;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import database.GroupsStorage;
import handlers.vk.Vk;;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Класс получающий обновления постов в группах для консольного пользователя
 *
 * @author Кедровских Олег
 * @version 1.3
 * @see PostsPullingThread
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
     * @param consoleUserId id консольного пользователя в database
     * @param groupsStorage база данных групп на которые оформленна подписка
     */
    public ConsolePostsPullingThread(String consoleUserId, GroupsStorage groupsStorage, Vk vk) {
        super(groupsStorage, vk);
        this.consoleBotUserId = consoleUserId;
    }

    /**
     * Метод логики выполняемой внутри {@code ConsoleBotPullingThread}
     */
    @Override
    public void run() {
        while (working) {
            try {
                for (String groupScreenName : groupsBase.getUserSubscribedGroups(consoleBotUserId)) {
                    Optional<List<String>> threadNewPosts = vk.getNewPosts(groupsBase, groupScreenName);

                    if (threadNewPosts.isPresent()) {
                        List<String> threadFindNewPosts = threadNewPosts.get();
                        for (int i = 0; i < threadFindNewPosts.size(); i++) {
                            try {
                                synchronized (newPostsQueue) {
                                    newPostsQueue.add(threadFindNewPosts.get(i));
                                    continue;
                                }
                            } catch (IllegalStateException ignored) {
                            }
                            i--;
                        }
                    }

                }
                final int oneHourInMilliseconds = 360000;
                Thread.sleep(oneHourInMilliseconds);
            } catch (InterruptedException e) {
                break;
            } catch (ApiException | ClientException ignored) {
            }
        }
        working = false;
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @return {@code true} - если есть новые посты, {@code false} - если нет новых постов
     */
    @Override
    public boolean hasNewPosts() {
        return !newPostsQueue.isEmpty();
    }

    /**
     * Метод получающий новые посты
     *
     * @return список новых постов в виде списка строк
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
