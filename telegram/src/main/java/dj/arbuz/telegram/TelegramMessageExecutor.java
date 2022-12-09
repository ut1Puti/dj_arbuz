package dj.arbuz.telegram;

import dj.arbuz.database.group.GroupService;
import dj.arbuz.database.user.UserService;

import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
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
     * @see TelegramPostsPullingTask
     */
    private final ScheduledExecutorService telegramPostsPullingThread = Executors.newScheduledThreadPool(1);

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot экземпляр телеграм бота
     */
    public TelegramMessageExecutor(TelegramBot telegramBot) {
        UserBase userStorage = new UserService();
        GroupBase groupsStorage = new GroupService();
        Vk vk = new Vk(new OAuthCodeQueue(HttpServer.getInstance()));
        messageHandler = new MessageHandlerImpl(groupsStorage, userStorage, vk);
        messageSender = new TelegramMessageSender(telegramBot);
        telegramPostsPullingThread.scheduleAtFixedRate(new TelegramPostsPullingTask(telegramBot, groupsStorage, vk), 0, 1, TimeUnit.HOURS);
    }

    /**
     * Метод исполняющий текстовое сообщение пользователя
     *
     * @param userReceivedId        id пользователя от которого было получено сообщение
     * @param userReceivedMessage   полученное от пользователя сообщение
     */
    public void executeUserMessage(String userReceivedId, String userReceivedMessage) {
        MessageHandlerResponse response = messageHandler.handleMessage(userReceivedMessage, userReceivedId);
        messageSender.sendResponse(response);
    }

    /**
     * Метод останавливающий работу исполнителя
     */
    public void stop() {
        telegramPostsPullingThread.shutdown();
    }
}
