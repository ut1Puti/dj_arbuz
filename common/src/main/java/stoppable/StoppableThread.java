package stoppable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс останавливаемых потоков
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see Thread
 * @see Stoppable
 */
public class StoppableThread extends Thread implements Stoppable {
    /**
     * Поле показывающее находится ли поток в бесконечном цикле
     */
    protected volatile AtomicBoolean working = new AtomicBoolean();

    /**
     * Конструктор - создает экземпляр класса
     *
     * @see Thread#Thread()
     */
    public StoppableThread() {

    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadTask - логика выполняемая внутри потока
     * @see Thread#Thread(Runnable)
     */
    public StoppableThread(Runnable threadTask) {
        super(threadTask);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask          - логика выполняемая внутри потока
     * @see Thread#Thread(ThreadGroup, Runnable)
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask) {
        super(threadRelatingGroup, threadTask);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param name - имя потока
     * @see Thread#Thread(String)
     */
    public StoppableThread(String name) {
        super(name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param name                - имя потока
     * @see Thread#Thread(ThreadGroup, String)
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, String name) {
        super(threadRelatingGroup, name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadTask - логика выполняемая внутри потока
     * @param name       - имя потока
     * @see Thread#Thread(Runnable, String)
     */
    public StoppableThread(Runnable threadTask, String name) {
        super(threadTask, name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask          - логика выполняемая внутри потока
     * @param name                - имя потока
     * @see Thread#Thread(ThreadGroup, Runnable, String)
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask, String name) {
        super(threadRelatingGroup, threadTask, name);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask          - логика выполняемая внутри потока
     * @param name                - имя потока
     * @param stackSize           - максимальный размер стека
     * @see Thread#Thread(ThreadGroup, Runnable, String, long)
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask, String name, long stackSize) {
        super(threadRelatingGroup, threadTask, name, stackSize);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param threadRelatingGroup - группа потоков к которой будет относиться новый поток
     * @param threadTask          - логика выполняемая внутри потока
     * @param name                - имя потока
     * @param stackSize           - максимальный размер стека
     * @param inheritThreadLocals - true, если наследуем начальные значения
     *                            для наследуемых локальных переменных потока из потока кнструктора,
     *                            в противном случае начальные значения не наследуются
     * @see Thread#Thread(ThreadGroup, Runnable, String, long, boolean)
     */
    public StoppableThread(ThreadGroup threadRelatingGroup, Runnable threadTask, String name,
                           long stackSize, boolean inheritThreadLocals) {
        super(threadRelatingGroup, threadTask, name, stackSize, inheritThreadLocals);
    }

    /**
     * Метод запускающий {@code StoppableThread}
     *
     * @see Thread#start()
     */
    @Override
    public final void start() {
        working.set(true);
        super.start();
    }

    /**
<<<<<<< HEAD
     * Метод выполняющий логику выполняемую внутри потока, наследники должны иметь внутри себя цикл
     * {@code while(working.get) {some logic that Thread do} working.set(false);}, этот цикл реализует бесконечный цикл
     * внутри которого будет исполняться логика потока
     */
    @Override
    public void run() {
        while (working.get()) Thread.onSpinWait();
        working.set(false);
    }

    /**
     * Метод проверяющий работает ли {@code StoppableThread}
     *
     * @return {@code true} если поток жив и находиться в бесконечном цикле исполнения,
     * {@code false} если поток завершил работу или завершит по окончании текущего цикла
     * @see Thread#isAlive()
     */
    @Override
    public boolean isWorking() {
        return this.isAlive() && working.get();
    }

    /**
     * Метод останавливающий {@code StoppableThread}
     *
     * @see Thread#interrupt()
     */
    @Override
    public void stopWithInterrupt() {
        working.set(false);
        this.interrupt();
    }
}
