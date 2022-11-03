package handlers.messages;

import bots.telegram.TelegramBot;

/**
 * Класс-отправитель сообщений телеграм бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see MessageSender
 */
public class TelegramMessageSender extends MessageSender {
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot бот, от имени которого будет отправлено сообщение
     */
    public TelegramMessageSender(TelegramBot telegramBot) {
        super(telegramBot);
    }
}
