package handlers.messages;

import bots.BotMessageExecutable;
import database.UserBase;
import database.UserStorage;

import java.util.function.BiConsumer;

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
    public TelegramMessageSender(BotMessageExecutable telegramBot, UserBase userStorage) {
        super(telegramBot, userStorage);
    }
}
