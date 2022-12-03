package dj.arbuz.console;

import dj.arbuz.handlers.messages.AbstractMessageSender;
import dj.arbuz.handlers.messages.MessageHandlerResponse;

import java.util.List;

/**
 * Класс-отправитель сообщений консольного бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractMessageSender
 */
public final class ConsoleMessageSender extends AbstractMessageSender {
    /**
     * Поле потока, получающего новые посты для консольного бота
     *
     * @see ConsolePostsPullingThread
     */
    private final ConsolePostsPullingTask notificationPullingTask;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBot                консольный бот
     * @param notificationPullingTask поток получающий новые посты в группах
     */
    public ConsoleMessageSender(ConsoleBot consoleBot, ConsolePostsPullingTask notificationPullingTask) {
        super(consoleBot);
        this.notificationPullingTask = notificationPullingTask;
    }

    /**
     * Метод для отправки сообщений пользователю
     *
     * @param userSendResponse ответ бота, который необходимо отправить пользователю
     */
    @Override
    public void sendResponse(MessageHandlerResponse userSendResponse) {
        super.sendResponse(userSendResponse);
        List<String> usersSendResponseId = userSendResponse.getUsersSendResponseId();

        if (notificationPullingTask.hasNewPosts()) {
            for (String newPostText : notificationPullingTask.getNewPosts()) {
                for (String userSendResponseId : usersSendResponseId) {
                    this.sendSingleMessage(userSendResponseId, newPostText);
                }
            }
        }

    }
}
