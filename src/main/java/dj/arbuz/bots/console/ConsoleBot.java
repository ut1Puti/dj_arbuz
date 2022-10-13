package dj.arbuz.bots.console;

/**
 * Класс консольного бота
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public class ConsoleBot {
    /**
     * Поле id пользователя консольной версии бота
     */
    public static final String defaultConsoleUserId = "-10";
    /**
     * Поле потока обрабатывающего и получающего сообщения пользователей
     */
    private ConsoleBotThread consoleBotThread = new ConsoleBotThread(defaultConsoleUserId);

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
     * false - если поток обрабатывающий сообщения завершил свою работу
     */
    public boolean isWorking() {
        return consoleBotThread.isWorking();
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop() {
        consoleBotThread.stopWithInterrupt();
    }
}
