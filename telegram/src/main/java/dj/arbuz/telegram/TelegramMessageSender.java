package dj.arbuz.telegram;

import dj.arbuz.BotMessageExecutable;
import dj.arbuz.AbstractMessageSender;
import dj.arbuz.handlers.messages.MessageHandlerResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Override
    protected void sendAdditionalMessage(MessageHandlerResponse response, List<String> usersSendResponseId) {
        CompletableFuture.runAsync(() -> super.sendAdditionalMessage(response, usersSendResponseId));
    }
}
