package dj.arbuz.telegram;

import dj.arbuz.BotMessageExecutable;
import dj.arbuz.handlers.messages.AbstractMessageSender;

/**
 * Класс-отправитель сообщений телеграм бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractMessageSender
 */
public final class TelegramMessageSender extends AbstractMessageSender {
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot бот, от имени которого будет отправлено сообщение
     */
    public TelegramMessageSender(BotMessageExecutable telegramBot) {
        super(telegramBot);
    }
}
