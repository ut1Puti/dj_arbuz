package handlers.messages;

import bots.telegram.TelegramBot;
import database.UserStorage;
import handlers.messages.AbstractMessageSender;

/**
 * Класс-отправитель сообщений телеграм бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractMessageSender
 */
public class TelegramMessageSender extends AbstractMessageSender {
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot бот, от имени которого будет отправлено сообщение
     */
    public TelegramMessageSender(TelegramBot telegramBot, UserStorage userStorage) {
        super(telegramBot, userStorage);
    }
}
