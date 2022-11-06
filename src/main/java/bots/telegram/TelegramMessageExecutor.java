package bots.telegram;

import bots.StoppableByUser;
import database.GroupsStorage;
import database.UserStorage;
import handlers.messages.MessageHandleable;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.messages.TelegramMessageSender;
import handlers.notifcations.TelegramPostsPullingThread;
import socialnetworks.vk.Vk;

/**
 * Класс исполнителя сообщений пользователя телеграм бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class TelegramMessageExecutor {
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageHandleable
     */
    private final MessageHandleable messageHandler;
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
        UserStorage userStorage = UserStorage.getInstance();
        GroupsStorage groupsStorage = GroupsStorage.getInstance();
        Vk vk = new Vk();
        messageHandler = new MessageHandler(groupsStorage, userStorage, vk);
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
     * @param userReceivedId id пользователя от которого было получено сообщение
     * @param userReceivedMessage полученное от пользователя сообщение
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
        UserStorage.getInstance().saveToJsonFile();
        GroupsStorage.getInstance().saveToJsonFile();
    }
}
