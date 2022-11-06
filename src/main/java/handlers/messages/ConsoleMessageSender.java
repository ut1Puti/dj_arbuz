package handlers.messages;

import bots.console.ConsoleBot;
import database.UserStorage;
import handlers.messages.AbstractMessageSender;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.ConsolePostsPullingThread;

/**
 * Класс-отправитель сообщений консольного бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractMessageSender
 */
public class ConsoleMessageSender extends AbstractMessageSender {
    /**
     * Поле потока, получающего новые посты для консольного бота
     *
     * @see ConsolePostsPullingThread
     */
    private final ConsolePostsPullingThread notificationPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBot бот, от имени которого будет отправлено сообщение
     * @param userStorage хранилище пользователей
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
        String userSendResponseId = userSendResponse.getUserSendResponseId();

        if (notificationPullingThread.hasNewPosts()) {
            for (String newPostText : notificationPullingThread.getNewPosts()) {
                this.sendSingleMessage(userSendResponseId, newPostText);
            }
        }

    }
}
