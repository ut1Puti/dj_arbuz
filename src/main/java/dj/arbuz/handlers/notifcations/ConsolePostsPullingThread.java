package dj.arbuz.handlers.notifcations;

import dj.arbuz.database.local.GroupsStorage;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.vk.Vk;

import java.util.List;
import java.util.Optional;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Класс получающий обновления постов в группах для консольного пользователя
 *
 * @author Кедровских Олег
 * @version 1.7
 * @see PostsPullingThread
 */
public final class ConsolePostsPullingThread extends PostsPullingThread {
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
     * @param socialNetwork интерфейс социальной сети реализующий необходимые методы
     */
    public ConsolePostsPullingThread(String consoleUserId, GroupsStorage groupsStorage, Vk socialNetwork) {
        super(groupsStorage, socialNetwork);
        this.consoleBotUserId = consoleUserId;
    }

    /**
     * Метод логики выполняемой внутри {@code ConsoleBotPullingThread}
     */
    @Override
    public void run() {
        while (working.get()) {
            for (String groupScreenName : groupsBase.getUserSubscribedGroups(consoleBotUserId)) {
                Optional<List<String>> threadNewPosts;
                try {
                    threadNewPosts = getNewPostsAsStrings(groupScreenName);
                } catch (SocialNetworkException e) {
                    continue;
                }

                // проверяется наличие новых постов, могут отсутствовать по причине отсутствия новых постов или отсутствия группы в базе данных
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
            try {
                final int oneHourInMilliseconds = 360000;
                Thread.sleep(oneHourInMilliseconds);
            } catch (InterruptedException e) {
                break;
            }
        }
        working.set(false);
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
