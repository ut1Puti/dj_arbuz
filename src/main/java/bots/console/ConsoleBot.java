package bots.console;

import bots.BotMessageExecutable;
import database.GroupsStorage;
import database.UserStorage;
import handlers.messages.ConsoleMessageExecutor;
import handlers.messages.MessageHandleable;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.ConsolePostsPullingThread;
import bots.StoppableByUser;
import httpserver.server.HttpServer;
import socialnetworks.socialnetwork.SocialNetwork;
import socialnetworks.vk.Vk;
import stoppable.StoppableThread;

import java.util.Scanner;

/**
 * Класс потока обрабатывающего сообщения пользователя с консоли
 *
 * @author Кедровских Олег
 * @version 1.2
 * @see StoppableThread
 * @see StoppableByUser
 */
public class ConsoleBot extends StoppableThread implements StoppableByUser, BotMessageExecutable {

    /**
     * Поле id пользователя консольной версии бота
     */
    private final String defaultConsoleUserId = "consoleUser";
    /**
     * Поле хранящее пользователя пользующегося ботом
     *
     * @see UserStorage
     */
    private final UserStorage userBase;
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageHandleable
     */
    private final MessageHandleable messageHandler;
    /**
     * Поле класса-отправителя сообщений пользователю
     *
     * @see ConsoleMessageExecutor
     */
    private final ConsoleMessageExecutor messageExecutor;
    /**
     * Поле класса получающего новые посты
     *
     * @see ConsolePostsPullingThread
     */
    private final ConsolePostsPullingThread consolePostsPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param userStorage   база данных пользователей
     * @param groupsStorage база данных групп
     * @param socialNetwork социальная сеть с которой будет работать бот
     */
    public ConsoleBot(UserStorage userStorage, GroupsStorage groupsStorage, SocialNetwork socialNetwork) {
        this.userBase = userStorage;
        this.messageHandler = new MessageHandler(groupsStorage, userStorage, socialNetwork);
        this.messageExecutor = new ConsoleMessageExecutor(this);
        this.consolePostsPullingThread = new ConsolePostsPullingThread(defaultConsoleUserId, groupsStorage, socialNetwork);
    }

    public static void main(String[] args) {
        HttpServer httpServer = HttpServer.getInstance();

        if (httpServer == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        httpServer.start();

        SocialNetwork vk = new Vk();
        UserStorage userStorage = UserStorage.getInstance();
        GroupsStorage groupsStorage = GroupsStorage.getInstance();
        ConsoleBot consoleBot = new ConsoleBot(userStorage, groupsStorage, vk);
        consoleBot.start();
        while (consoleBot.isWorking()) {
            Thread.onSpinWait();
        }
        consoleBot.stopWithInterrupt();
        httpServer.stop();
        userStorage.saveToJsonFile();
        groupsStorage.saveToJsonFile();
    }

    /**
     * Метод с логикой выполняемой внутри потока
     *
     * @see StoppableThread#run()
     * @see MessageHandler#handleMessage(String, String, StoppableByUser)
     * @see ConsolePostsPullingThread#start()
     * @see MessageHandlerResponse#hasTextMessage()
     * @see MessageHandlerResponse#getTextMessage()
     * @see MessageHandlerResponse#hasPostsMessages()
     * @see MessageHandlerResponse#getPostsMessages()
     * @see MessageHandlerResponse#hasUpdateUser()
     * @see MessageHandlerResponse#getUpdateUser()
     * @see ConsolePostsPullingThread#stopWithInterrupt()
     */
    @Override
    public void run() {
        consolePostsPullingThread.start();
        Scanner userInput = new Scanner(System.in);
        while (working.get()) {

            if (userInput.hasNextLine()) {
                MessageHandlerResponse response = messageHandler.handleMessage(userInput.nextLine(), defaultConsoleUserId, this);
                messageExecutor.executeMessage(response, userBase);
            }

            if (consolePostsPullingThread.hasNewPosts()) {
                consolePostsPullingThread.getNewPosts().forEach(System.out::println);
            }

        }
        working.set(false);
        userInput.close();
        consolePostsPullingThread.stopWithInterrupt();
    }

    /**
     * Реализация интерфейса для отправки сообщения пользователю, выводит сообщение в консоль
     *
     * @param userSendResponseId  id пользователя, которому необходимо отправить сообщение
     * @param responseSendMessage сообщение, которое будет отправлено пользователю
     */
    @Override
    public void execute(String userSendResponseId, String responseSendMessage) {

        if (!userSendResponseId.equals(defaultConsoleUserId)) {
            return;
        }

        System.out.println(responseSendMessage);
    }

    /**
     * Метод реализующий интерфейс для остановки бота пользователем
     */
    @Override
    public void stopByUser() {
        stopWithInterrupt();
    }
}
