package bots.consolebot;

import bots.BotTextResponse;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.Notifications;
import handlers.notifcations.NotificationsPullingThread;
import user.User;

import java.util.Scanner;

/**
 * Класс консольного бота
 *
 * @author Кедровских Олег
 * @version 1.6
 */
public class ConsoleBot {
    /**
     * Поле показывающее работает ли бот
     */
    private boolean working;
    /**
     * Поле хранящее пользователя пользующегося ботом
     */
    private User currentUser = null;
    /**
     * Поле класса получающего новые посты
     */
    private final Notifications notifications = new Notifications();

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     * Работает до тех пор, пока пользователь не прекратит работу бота.
     */
    public void run() {
        working = true;
        notifications.start();
        Scanner userInput = new Scanner(System.in);
        while (working) {

            if (userInput.hasNextLine()) {
                MessageHandlerResponse response = MessageHandler.executeMessage(userInput.nextLine(), currentUser, this);

                if (response.hasTextMessage()) {
                    System.out.println(response.getTextMessage());
                }

                if (response.hasUpdateUser()) {
                    currentUser = response.getUpdateUser().createUser();

                    if (currentUser == null) {
                        System.out.println(BotTextResponse.AUTH_ERROR);
                    }

                }

            }

            if (notifications.hasNewPosts()) {
                notifications.getNewPosts().forEach(newPosts -> newPosts.forEach(System.out::println));
            }

        }
        userInput.close();
        notifications.stop();
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop() {
        working = false;
    }
}
