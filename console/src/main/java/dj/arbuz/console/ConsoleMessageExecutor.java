package dj.arbuz.console;

import dj.arbuz.database.GroupsStorage;
import dj.arbuz.database.UserStorage;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerImpl;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
import dj.arbuz.socialnetworks.vk.Vk;
import dj.arbuz.socialnetworks.vk.oAuth.OAuthCodeQueue;
import httpserver.HttpServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
     *
     */
    private final ConsolePostsPullingTask consolePostsPullingTask;
    /**
     * Поле потока получения уведомлений о новых постах
     *
     * @see ConsolePostsPullingTask
     */
    private final ScheduledExecutorService consolePostsPullingThread = Executors.newScheduledThreadPool(1);

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBot экземпляр консольного бота
     */
    public ConsoleMessageExecutor(ConsoleBot consoleBot) {
        UserStorage userStorage = UserStorage.getInstance();
        GroupsStorage groupsStorage = GroupsStorage.getInstance();
        Vk vk = new Vk(new OAuthCodeQueue(HttpServer.getInstance()));
        messageHandler = new MessageHandlerImpl(groupsStorage, userStorage, vk);
        consolePostsPullingTask = new ConsolePostsPullingTask(consoleBot.getName(), groupsStorage, vk);
        consolePostsPullingThread.scheduleAtFixedRate(consolePostsPullingTask, 0, 1, TimeUnit.HOURS);
        messageSender = new ConsoleMessageSender(consoleBot, consolePostsPullingTask);
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
        consolePostsPullingThread.shutdown();
        UserStorage.getInstance().saveToJsonFile();
        GroupsStorage.getInstance().saveToJsonFile();
    }
}
