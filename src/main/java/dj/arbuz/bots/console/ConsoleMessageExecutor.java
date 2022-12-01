<<<<<<<< HEAD:console/src/main/java/dj/arbuz/console/ConsoleMessageExecutor.java
package dj.arbuz.console;

import dj.arbuz.database.GroupsStorage;
import dj.arbuz.database.UserStorage;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerImpl;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
========
package dj.arbuz.bots.console;

import dj.arbuz.bots.StoppableByUser;
import dj.arbuz.database.local.GroupsStorage;
import dj.arbuz.database.local.UserStorage;
import dj.arbuz.handlers.messages.ConsoleMessageSender;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerImpl;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
import dj.arbuz.handlers.notifcations.ConsolePostsPullingThread;
>>>>>>>> developTaskFour:src/main/java/dj/arbuz/bots/console/ConsoleMessageExecutor.java
import dj.arbuz.socialnetworks.vk.Vk;

/**
 * Класс исполнителя сообщений пользователя консольного бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public final class ConsoleMessageExecutor {
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageHandler
     */
    private final MessageHandler messageHandler;
    /**
     * Поле отправителя ответов пользователям
     *
     * @see ConsoleMessageSender
     */
    private final ConsoleMessageSender messageSender;
    /**
     * Поле потока получения уведомлений о новых постах
     *
     * @see ConsolePostsPullingThread
     */
    private final ConsolePostsPullingThread notificationPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBot экземпляр консольного бота
     */
    public ConsoleMessageExecutor(ConsoleBot consoleBot) {
        UserStorage userStorage = UserStorage.getInstance();
        GroupsStorage groupsStorage = GroupsStorage.getInstance();
        Vk vk = new Vk();
        messageHandler = new MessageHandlerImpl(groupsStorage, userStorage, vk);
        notificationPullingThread = new ConsolePostsPullingThread(consoleBot.getName(), groupsStorage, vk);
        messageSender = new ConsoleMessageSender(consoleBot, notificationPullingThread);
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
     */
    public void executeTextMessage(String userReceivedId, String userReceivedMessage) {
        MessageHandlerResponse response = messageHandler.handleMessage(userReceivedMessage, userReceivedId);
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
