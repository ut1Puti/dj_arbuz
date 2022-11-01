package handlers.messages;

import bots.telegram.TelegramBot;

/**
 * Класс-отправитель сообщений телеграм бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class TelegramMessageExecutor extends MessageExecutor {
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot бот, от имени которого будет отправлено сообщение
     */
    public TelegramMessageExecutor(TelegramBot telegramBot) {
        super(telegramBot);
    }
}
