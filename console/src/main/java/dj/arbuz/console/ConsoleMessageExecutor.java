package dj.arbuz.console;

import dj.arbuz.database.GroupsStorage;
import dj.arbuz.database.UserStorage;
import dj.arbuz.StoppableByUser;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerImpl;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
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
        messageSender = new ConsoleMessageSender(consoleBot, userStorage, notificationPullingThread);
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
    public void executeTextMessage(String userReceivedId, String userReceivedMessage, StoppableByUser stoppableByUserThread) {
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
