package handlers.messages;

import bots.console.ConsoleBot;
import database.UserStorage;
import handlers.notifcations.ConsolePostsPullingThread;

/**
 * Класс-отправитель сообщений консольного бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see MessageSender
 */
public class ConsoleMessageSender extends MessageSender {
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
     */
    public ConsoleMessageSender(ConsoleBot consoleBot, ConsolePostsPullingThread notificationPullingThread) {
        super(consoleBot);
        this.notificationPullingThread = notificationPullingThread;
    }

    /**
     * Метод для отправки сообщений пользователю
     *
     * @param response ответ бота, который необходимо отправить пользователю
     * @param userBase база данных пользователей
     */
    @Override
    public void executeMessage(MessageHandlerResponse response, UserStorage userBase) {
        super.executeMessage(response, userBase);
        String userSendResponseId = response.getUserSendResponseId();

        if (notificationPullingThread.hasNewPosts()) {
            for (String newPostText : notificationPullingThread.getNewPosts()) {
                this.executeSingleMessage(userSendResponseId, newPostText);
            }
        }

    }
}
