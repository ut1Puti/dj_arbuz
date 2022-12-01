<<<<<<<< HEAD:telegram/src/main/java/dj/arbuz/telegram/TelegramMessageSender.java
package dj.arbuz.telegram;

import dj.arbuz.BotMessageExecutable;
import dj.arbuz.handlers.messages.AbstractMessageSender;
========
package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotMessageExecutable;
import dj.arbuz.database.UserBase;
>>>>>>>> developTaskFour:src/main/java/dj/arbuz/handlers/messages/TelegramMessageSender.java

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
