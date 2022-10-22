package handlers.notifcations;

import bots.telegram.TelegramBot;

import java.util.List;

/**
 * Класс получения новых постов
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class NotificationsPuller {
    /**
     * Поле потока ищущего новые посты
     *
     * @see PostsPullingThread
     */
    private final PostsPullingThread postsPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     */
    public NotificationsPuller(String consoleBotUserId) {
        postsPullingThread = new ConsolePostsPullingThread(consoleBotUserId);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot - бот в телеграмме
     */
    public NotificationsPuller(TelegramBot telegramBot) {
        postsPullingThread = new TelegramPostsPullingThread(telegramBot);
    }

    /**
     * Метод запускающий {@code postsPullingThread}
     *
     * @see PostsPullingThread#start()
     */
    public void start() {
        postsPullingThread.start();
    }

    /**
     * Метод проверяющий наличие новых постов в потоке
     *
     * @return {@code true} - если поток нашел новые посты, {@code false} - если поток не нашел новых постов
     * @see PostsPullingThread#hasNewPosts()
     */
    public boolean hasNewPosts() {
        return postsPullingThread.hasNewPosts();
    }

    /**
     * Метод получающий новые посты найденные в потоке
     *
     * @return список списков постов в виде строк
     * @see PostsPullingThread#getNewPosts()
     */
    public List<String> getNewPosts() {
        return postsPullingThread.getNewPosts();
    }

    /**
     * Метод проверяющий работает ли {@code postsPullingThread}
     *
     * @return true - если {@code postsPullingThread} работает, false - если {@code postsPullingThread} завершил работу
     * @see PostsPullingThread#isWorking()
     */
    public boolean isWorking() {
        return postsPullingThread.isWorking();
    }

    /**
     * Метод останавливающий {@code postsPullingThread}
     *
     * @see PostsPullingThread#stopWithInterrupt()
     */
    public void stop() {
        postsPullingThread.stopWithInterrupt();
    }
}
