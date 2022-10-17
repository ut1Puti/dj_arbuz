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
     * Метод запускающий поток ищущий новые посты
     */
    public void start() {
        //TODO ограничить кол-во уведомлений получаемых пользователей за первый запрос
        postsPullingThread.start();
    }

    /**
     * Метод проверяющий наличие новых постов в потоке
     *
     * @return true - если поток нашел новые посты
     * false - если поток не нашел новых постов
     */
    public boolean hasNewPosts() {
        return postsPullingThread.hasNewPosts();
    }

    /**
     * Метод получающий новые посты найденные в потоке
     *
     * @return список списков постов в виде строк
     */
    public List<String> getNewPosts() {
        return postsPullingThread.getNewPosts();
    }

    /**
     * Метод проверяющий работает ли поток для получения новых постов
     *
     * @return true - если поток работает
     * false - если поток завершил работу
     */
    public boolean isWorking() {
        return postsPullingThread.isWorking();
    }

    /**
     * Метод останавливающий работу потока
     */
    public void stop() {
        postsPullingThread.stopWithInterrupt();
    }
}
