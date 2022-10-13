package dj.arbuz.bots.console;

import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.database.UserStorage;
import dj.arbuz.handlers.messages.MessageHandler;
import dj.arbuz.handlers.messages.MessageHandlerResponse;
import dj.arbuz.handlers.notifcations.NotificationsPuller;
import dj.arbuz.stoppable.StoppableThread;
import dj.arbuz.user.User;

import java.util.Scanner;

/**
 * Класс потока обрабатывающего сообщения пользователя с консоли
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ConsoleBotThread extends StoppableThread {
    /**
     * Поле id пользователя консольной версии бота
     */
    private final String consoleBotUserId;
    /**
     * Поле хранящее пользователя пользующегося ботом
     */
    private UserStorage userBase = UserStorage.getInstance();
    /**
     * Поле класса получающего новые посты
     */
    private final NotificationsPuller notificationsPuller;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consoleBotUserId - id консольного пользователя бота
     */
    public ConsoleBotThread(String consoleBotUserId) {
        this.consoleBotUserId = consoleBotUserId;
        notificationsPuller = new NotificationsPuller(consoleBotUserId);
    }

    /**
     * Метод с логикой выполняемой внутри потока
     */
    @Override
    public void run() {
        working = true;
        notificationsPuller.start();
        Scanner userInput = new Scanner(System.in);
        while (working) {

            if (userInput.hasNextLine()) {
                MessageHandlerResponse response = MessageHandler.executeMessage(
                        userInput.nextLine(), consoleBotUserId, this
                );

                if (response.hasTextMessage()) {
                    System.out.println(response.getTextMessage());
                }

                if (response.hasUpdateUser()) {
                    User currentUser = response.getUpdateUser().createUser(consoleBotUserId);

                    if (currentUser == null) {
                        System.out.println(BotTextResponse.AUTH_ERROR);
                        continue;
                    }

                    System.out.println(BotTextResponse.AUTH_SUCCESS);

                    userBase.addInfoUser(consoleBotUserId, currentUser);
                }

            }

            if (notificationsPuller.hasNewPosts()) {
                notificationsPuller.getNewPosts().forEach(System.out::println);
            }

        }
        userInput.close();
        notificationsPuller.stop();
    }
}
