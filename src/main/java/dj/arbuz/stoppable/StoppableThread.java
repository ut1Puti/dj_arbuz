package dj.arbuz.stoppable;

/**
 * Класс останавливаемых потоков
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class StoppableThread extends Thread implements Stoppable {
    /**
     * Поле показывающее находится ли поток в бесконечном цикле
     */
    protected volatile boolean working = false;

    /**
     * Конструктор - создает экземпляр класса
     */
    public StoppableThread() {

    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadTask - логика выполняемая внутри потока
     */
    public StoppableThread(Runnable threadTask) {
        super(threadTask);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask - логика выполняемая внутри потока
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask) {
        super(threadRelatingGroup, threadTask);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param name - имя потока
     */
    public StoppableThread(String name) {
        super(name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param name - имя потока
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, String name) {
        super(threadRelatingGroup, name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadTask - логика выполняемая внутри потока
     * @param name - имя потока
     */
    public StoppableThread(Runnable threadTask, String name) {
        super(threadTask, name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask - логика выполняемая внутри потока
     * @param name - имя потока
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask, String name) {
        super(threadRelatingGroup, threadTask, name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask - логика выполняемая внутри потока
     * @param name - имя потока
     * @param stackSize - максимальный размер стека
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask, String name, long stackSize) {
        super(threadRelatingGroup, threadTask, name, stackSize);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask - логика выполняемая внутри потока
     * @param name - имя потока
     * @param stackSize - максимальный размер стека
     * @param inheritThreadLocals - true, если наследуем начальные значения
     *                            для наследуемых локальных переменных потока из потока кнструктора,
     *                            в противном случае начальные значения не наследуются
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask, String name,
                           long stackSize, boolean inheritThreadLocals) {
        super(threadRelatingGroup, threadTask, name, stackSize, inheritThreadLocals);
    }

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
