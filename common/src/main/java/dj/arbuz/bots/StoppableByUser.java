package dj.arbuz.bots;

/**
 * Интерфейс для остановки объекта пользователем
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface StoppableByUser {
    /**
     * Метод останавливающий работу объекта при запросе от пользователя
     */
    void stopByUser();
}
