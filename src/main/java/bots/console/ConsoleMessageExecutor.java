package bots.console;

import bots.StoppableByUser;
import database.GroupsStorage;
import database.UserStorage;
import handlers.messages.ConsoleMessageSender;
import handlers.messages.MessageHandleable;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.ConsolePostsPullingThread;
import socialnetworks.vk.Vk;

import java.util.function.BiConsumer;

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
     * @see MessageHandleable
     */
    private final MessageHandleable messageHandler;
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
        messageHandler = new MessageHandler(groupsStorage, userStorage, vk);
        notificationPullingThread = new ConsolePostsPullingThread(consoleBot.getName(), groupsStorage, vk);
        BiConsumer<String, String> consumer = new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                if (!s.equals(consoleBot.getName())) {
                    return;
                }

                System.out.println(s2);
            }
        };
        messageSender = new ConsoleMessageSender(consumer, userStorage, notificationPullingThread);
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
