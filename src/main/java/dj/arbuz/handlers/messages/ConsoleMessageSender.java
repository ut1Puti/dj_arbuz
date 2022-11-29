package dj.arbuz.handlers.messages;

import dj.arbuz.bots.console.ConsoleBot;
import dj.arbuz.database.local.UserStorage;
import dj.arbuz.handlers.notifcations.ConsolePostsPullingThread;

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
    private final ConsolePostsPullingThread notificationPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBot                консольный бот
     * @param userStorage               хранилище пользователей
     * @param notificationPullingThread поток получающий новые посты в группах
     */
    public ConsoleMessageSender(ConsoleBot consoleBot, UserStorage userStorage, ConsolePostsPullingThread notificationPullingThread) {
        super(consoleBot, userStorage);
        this.notificationPullingThread = notificationPullingThread;
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

        if (notificationPullingThread.hasNewPosts()) {
            for (String newPostText : notificationPullingThread.getNewPosts()) {
                for (String userSendResponseId : usersSendResponseId) {
                    this.sendSingleMessage(userSendResponseId, newPostText);
                }
            }
        }

    }
}
