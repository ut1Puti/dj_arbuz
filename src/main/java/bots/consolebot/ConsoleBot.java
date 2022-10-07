package bots.consolebot;

import bots.BotTextResponse;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
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
    /** Поле показывающее работает ли бот */
    private boolean working;
    /** Поле хранящее пользователя пользующегося ботом */
    private User currentUser = null;
    /** Поле класса получающего новые посты */
    private final NotificationsPullingThread notificationsPuller = new NotificationsPullingThread();

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     * Работает до тех пор пока пользователь не прекратит работу бота.
     */
    public void run(){
        working = true;
        notificationsPuller.start();
        Scanner userInput = new Scanner(System.in);
        while (working) {

            if (!notificationsPuller.isAlive()) {
                notificationsPuller.start();
            }

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

            if (notificationsPuller.haveNewPosts()) {
                notificationsPuller.getNewPosts().forEach(newPosts -> newPosts.forEach(System.out::println));
            }

        }
        userInput.close();
        notificationsPuller._stop();
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop(){
        working = false;
    }
}
