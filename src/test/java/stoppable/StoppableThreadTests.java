package stoppable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stoppable.StoppableThread;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Класс тестрирующий работу останавливаемого потока
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class StoppableThreadTests {
    /**
     * Поле останавливаемого потока
     */
    private StoppableThread stoppableThread;

    /**
     * Метод создающий новый поток перед каждым тестом
     */
    @BeforeEach
    public void setUpStoppableThread() {
        stoppableThread = new StoppableThread(() -> {
            stoppableThread.working = true;
            while (stoppableThread.working);
        });
    }

    /**
     * Метод завершающий работу потока после каждого теста
     *
     * @throws InterruptedException - возникает при прерывании потока во время ожидания заверщения его работы
     */
    @AfterEach
    public void stopThreadAfterTest() throws InterruptedException {
        stoppableThread.stopWithInterrupt();
    }

    /**
     * Метод тестирующий работу метода, проверяющего работает ли поток
     */
    @Test
    public void testIsWorkingWhileThreadIsActive() throws InterruptedException {
        stoppableThread.start();
        assertTrue(stoppableThread.isWorking());
    }

    /**
     * Метод тестирующий работу метода, останавливающего работу потока
     */
    @Test
    public void testStopWithInterrupt() throws InterruptedException {
        stoppableThread.start();
        assertTrue(stoppableThread.isWorking());
        stoppableThread.stopWithInterrupt();
        //костыльный метод, может иногда не проходить
        Thread.sleep(1000);
        assertFalse(stoppableThread.isWorking());
    }
}
