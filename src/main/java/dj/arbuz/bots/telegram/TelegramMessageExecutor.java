package dj.arbuz.bots.telegram;

import dj.arbuz.bots.StoppableByUser;
import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.database.hibernate.group.GroupService;
import dj.arbuz.database.hibernate.user.UserService;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerImpl;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
import dj.arbuz.handlers.messages.TelegramMessageSender;
import dj.arbuz.handlers.notifcations.TelegramPostsPullingThread;
import dj.arbuz.socialnetworks.vk.Vk;

/**
 * Класс исполнителя сообщений пользователя телеграм бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public final class TelegramMessageExecutor {
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageHandler
     */
    private final MessageHandler messageHandler;
    /**
     * Поле отправителя ответов пользователям
     *
     * @see TelegramMessageSender
     */
    private final TelegramMessageSender messageSender;
    /**
     * Поле потока получения уведомлений о новых постах
     *
     * @see TelegramPostsPullingThread
     */
    private final TelegramPostsPullingThread notificationPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot экземпляр телеграм бота
     */
    public TelegramMessageExecutor(TelegramBot telegramBot) {
        UserBase userStorage = new UserService();
        GroupBase groupsStorage = new GroupService();
        Vk vk = new Vk();
        messageHandler = new MessageHandlerImpl(groupsStorage, userStorage, vk);
        messageSender = new TelegramMessageSender(telegramBot, userStorage);
        notificationPullingThread = new TelegramPostsPullingThread(telegramBot, groupsStorage, vk);
    }

    /**
     * Метод запускающий исполнитель
     */
    public void start() {
        notificationPullingThread.start();
    }

    /**
     * Метод исполняющий текстовое сообщение пользователя
     *
     * @param userReceivedId        id пользователя от которого было получено сообщение
     * @param userReceivedMessage   полученное от пользователя сообщение
     * @param stoppableByUserThread поток из которого было получено сообщение
     */
    public void executeUserMessage(String userReceivedId, String userReceivedMessage, StoppableByUser stoppableByUserThread) {
        MessageHandlerResponse response = messageHandler.handleMessage(userReceivedMessage, userReceivedId, stoppableByUserThread);
        messageSender.sendResponse(response);
    }

    /**
     * Метод останавливающий работу исполнителя
     */
    public void stop() {
        notificationPullingThread.stopWithInterrupt();
    }
}
