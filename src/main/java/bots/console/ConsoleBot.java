package bots.console;

import bots.BotStartInstances;
import bots.BotTextResponse;
import database.GroupsStorage;
import database.UserStorage;
import handlers.messages.MessageExecutable;
import handlers.messages.MessageExecutor;
import handlers.messages.MessageExecutorResponse;
import handlers.notifcations.ConsolePostsPullingThread;
import bots.StoppableByUser;
import handlers.notifcations.PostsPullingThread;
import socialnetworks.socialnetwork.SocialNetwork;
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
     * Поле хранящее пользователя пользующегося ботом
     *
     * @see UserStorage
     */
    private final UserStorage userBase;
    /**
     * Поле обработчика сообщений пользователя
     *
     * @see MessageExecutable
     */
    private final MessageExecutable messageExecutor;
    /**
     * Поле класса получающего новые посты
     *
     * @see ConsolePostsPullingThread
     */
    private final ConsolePostsPullingThread consolePostsPullingThread;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param botStartInstances набор объектов необходимых для запуска бота
     * @see ConsolePostsPullingThread#ConsolePostsPullingThread(String, GroupsStorage, SocialNetwork)
     */
    public ConsoleBot(BotStartInstances botStartInstances) {
        this.userBase = botStartInstances.userStorage;
        this.messageExecutor = botStartInstances.messageExecutor;
        this.consolePostsPullingThread = new ConsolePostsPullingThread(
                defaultConsoleUserId, botStartInstances.groupsStorage, botStartInstances.vk
        );
    }

    public static void main(String[] args) {
        BotStartInstances botStartInstances = new BotStartInstances("src/main/resources/anonsrc/vk_config.json");
        ConsoleBot consoleBot = new ConsoleBot(botStartInstances);
        consoleBot.start();
        while (consoleBot.isWorking()) Thread.onSpinWait();
        consoleBot.stopWithInterrupt();
        botStartInstances.stop();
    }

    /**
     * Метод с логикой выполняемой внутри потока
     *
     * @see StoppableThread#run()
     * @see MessageExecutor#executeMessage(String, String, StoppableByUser)
     * @see ConsolePostsPullingThread#start()
     * @see MessageExecutorResponse#hasTextMessage()
     * @see MessageExecutorResponse#getTextMessage()
     * @see MessageExecutorResponse#hasPostsMessages()
     * @see MessageExecutorResponse#getPostsMessages()
     * @see MessageExecutorResponse#hasUpdateUser()
     * @see MessageExecutorResponse#getUpdateUser()
     * @see ConsolePostsPullingThread#stopWithInterrupt()
     */
    @Override
    public void run() {
        consolePostsPullingThread.start();
        Scanner userInput = new Scanner(System.in);
        while (working.get()) {

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
     * Метод реализующий интерфейс для остановки бота пользователем
     */
    @Override
    public void stopByUser() {
        stopWithInterrupt();
    }
}
