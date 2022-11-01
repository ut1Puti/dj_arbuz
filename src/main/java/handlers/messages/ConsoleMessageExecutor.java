package handlers.messages;

import bots.console.ConsoleBot;

/**
 * Класс-отправитель сообщений консольного бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ConsoleMessageExecutor extends MessageExecutor {
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBot бот, от имени которого будет отправлено сообщение
     */
    public ConsoleMessageExecutor(ConsoleBot consoleBot) {
        super(consoleBot);
    }
}
