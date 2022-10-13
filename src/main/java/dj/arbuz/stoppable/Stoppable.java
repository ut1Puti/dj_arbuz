package dj.arbuz.stoppable;

/**
 * Интерфейс для остановки с помощью прерывания
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface Stoppable {
    /**
     * Метод проверяющий работает ли класс
     *
     * @return true - если работает
     * false - если не работает
     */
    boolean isWorking();

    /**
     * Метод останавливающий выполнение с помощью прерывания
     */
    void stopWithInterrupt();
}
