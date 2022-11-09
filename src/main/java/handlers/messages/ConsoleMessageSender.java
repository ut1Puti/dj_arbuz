package handlers.messages;

import bots.console.ConsoleBot;
import bots.telegram.TelegramBot;
import database.UserStorage;
import handlers.notifcations.ConsolePostsPullingThread;

import java.util.function.BiConsumer;

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
     * @param consoleBot
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
