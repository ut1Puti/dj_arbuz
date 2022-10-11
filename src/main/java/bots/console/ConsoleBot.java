package bots.console;

/**
 * Класс консольного бота
 *
 * @author Кедровских Олег
 * @version 1.9
 */
public class ConsoleBot {
    /**
     * Поле потока обрабатывающего и получающего сообщения пользователей
     */
    private ConsoleBotThread consoleBotThread = new ConsoleBotThread();

    /**
     * Метод запускающий поток обработки сообщений ботом
     */
    public void start() {
        consoleBotThread.start();
    }

    /**
     * Метод проверяющий работает ли консольный бот
     *
     * @return true - если поток обрабатывающий сообщения пользователя работает
     * false - ксли поток обрабатывающий сообщения завершил свою работу
     */
    public boolean isWorking() {
        return consoleBotThread.isAlive();
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop() {
        consoleBotThread.interrupt();
    }
}
