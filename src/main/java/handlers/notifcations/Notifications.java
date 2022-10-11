package handlers.notifcations;

import java.util.List;

/**
 * Класс получения новых постов
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class Notifications {
    /**
     * Поле потока ищущего новые посты
     */
    private final NotificationsPullingThread notificationsPullingThread = new NotificationsPullingThread();

    /**
     * Метод запускающий поток ищущий новые посты
     */
    public void start() {
        notificationsPullingThread.start();
    }

    /**
     * Метод проверяющий наличие новых постов в потоке
     *
     * @return true - если поток нашел новые посты
     *          false - если поток не нашел новых постов
     */
    public boolean hasNewPosts() {
        return notificationsPullingThread.hasNewPosts();
    }

    /**
     * Метод получающий новые посты найденные в потоке
     *
     * @return список списков постов в виде строк
     */
    public List<List<String>> getNewPosts() {
        return notificationsPullingThread.getNewPosts();
    }

    /**
     * Метод останавливающий работу потока
     */
    public void stop() {
        notificationsPullingThread.interrupt();
    }
}
