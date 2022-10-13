package dj.arbuz.stoppable;

/**
 * Класс останавливаемых потоков
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class StoppableThread extends Thread implements Stoppable{
    /**
     * Поле показывающее находится ли поток в бесконечном цикле
     */
    protected boolean working = false;

    /**
     * Метод проверяющий работает ли поток
     *
     * @return true - если поток работает
     * false - если поток завершил работу
     */
    @Override
    public boolean isWorking() {
        return isAlive() && working;
    }

    /**
     * Метод останавливающий работу потока
     */
    @Override
    public void stopWithInterrupt() {
        working = false;
        interrupt();
    }
}
