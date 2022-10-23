package bots.console;

import bots.BotStarterPack;
import bots.BotTextResponse;
import database.UserStorage;
import handlers.messages.MessageExecutor;
import handlers.messages.MessageExecutorResponse;
import handlers.notifcations.NotificationsPuller;
import bots.StoppableByUser;
import stoppable.StoppableThread;
import user.User;

import java.util.Scanner;

/**
 * Класс потока обрабатывающего сообщения пользователя с консоли
 *
 * @author Кедровских Олег
 * @version 1.2
 * @see StoppableThread
 * @see StoppableByUser
 */
public class ConsoleBot extends StoppableThread implements StoppableByUser {
    /**
     * Поле id пользователя консольной версии бота
     */
    private final String defaultConsoleUserId = "consoleUser";
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageExecutor
     */
    private final MessageExecutor messageExecutor;
    /**
     * Поле хранящее пользователя пользующегося ботом
     *
     * @see UserStorage
     */
    private final UserStorage userBase;
    /**
     * Поле класса получающего новые посты
     *
     * @see NotificationsPuller
     */
    private final NotificationsPuller notificationsPuller;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param botStarterPack набор объектов необходимых для запуска бота
     */
    public ConsoleBot(BotStarterPack botStarterPack) {
        this.messageExecutor = new MessageExecutor(botStarterPack.groupsStorage, botStarterPack.userStorage, botStarterPack.vk);
        this.userBase = botStarterPack.userStorage;
        this.notificationsPuller = new NotificationsPuller(defaultConsoleUserId, botStarterPack.groupsStorage, botStarterPack.vk);
    }

    public static void main(String[] args) {
        BotStarterPack botStarterPack = new BotStarterPack("src/main/resources/anonsrc/vk_config.json");
        ConsoleBot consoleBot = new ConsoleBot(botStarterPack);
        consoleBot.start();
        while (consoleBot.isWorking()) Thread.onSpinWait();
        consoleBot.stopWithInterrupt();
        botStarterPack.stop();
    }

    /**
     * Метод с логикой выполняемой внутри потока
     *
     * @see StoppableThread#run()
     * @see MessageExecutor#executeMessage(String, String, StoppableByUser)
     * @see NotificationsPuller#start()
     * @see MessageExecutorResponse#hasTextMessage()
     * @see MessageExecutorResponse#getTextMessage()
     * @see MessageExecutorResponse#hasPostsMessages()
     * @see MessageExecutorResponse#getPostsMessages()
     * @see MessageExecutorResponse#hasUpdateUser()
     * @see MessageExecutorResponse#getUpdateUser()
     * @see NotificationsPuller#stop()
     */
    @Override
    public void run() {
        notificationsPuller.start();
        Scanner userInput = new Scanner(System.in);
        while (working) {

            if (userInput.hasNextLine()) {
                MessageExecutorResponse response = messageExecutor.executeMessage(
                        userInput.nextLine(), defaultConsoleUserId, this
                );

                if (response.hasTextMessage()) {
                    System.out.println(response.getTextMessage());
                }

                if (response.hasPostsMessages()) {
                    response.getPostsMessages().forEach(System.out::println);
                }

                if (response.hasUpdateUser()) {
                    User currentUser = response.getUpdateUser().createUser(defaultConsoleUserId);

                    if (currentUser == null) {
                        System.out.println(BotTextResponse.AUTH_ERROR);
                        continue;
                    }

                    System.out.println(BotTextResponse.AUTH_SUCCESS);

                    userBase.addInfoUser(defaultConsoleUserId, currentUser);
                }

                if (response.isBotStopped()) {
                    stopWithInterrupt();
                }

            }

            if (notificationsPuller.hasNewPosts()) {
                notificationsPuller.getNewPosts().forEach(System.out::println);
            }

        }
        working = false;
        userInput.close();
        notificationsPuller.stop();
    }

    /**
     * Метод реализующий интерфейс для остановки бота пользователем
     */
    @Override
    public void stopByUser() {
        stopWithInterrupt();
    }
}
