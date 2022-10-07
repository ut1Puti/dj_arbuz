package bots;

import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.Notifications;
import user.User;

import java.util.Scanner;

/**
 * Класс консольного бота
 * @author Кедровских Олег
 * @version 1.0
 */
public class ConsoleBot {
    /** Поле показывающее работает ли бот */
    private boolean working;
    /** Поле хранящее пользователя пользующегося ботом */
    private User currentUser = null;
    /** Поле класса получающего новые посты */
    private Notifications notificationsPuller = new Notifications();

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     * Работает до тех пор пока пользователь не прекратит работу бота.
     * @param input Сканнер получаюший сообщения от пользователя
     */
    public void run(Scanner input){
        working = true;
        while (working) {
            MessageHandlerResponse response = MessageHandler.executeMessage(input.nextLine(), currentUser, this);

            if (response.hasTextMessage()) {
                System.out.println(response.getTextMessage());
            }

            if (response.hasUpdateUser()) {
                currentUser = response.getUpdateUser().createUser();

                if (currentUser == null) {
                    System.out.println(BotTextResponse.AUTH_ERROR);
                }

            }

            if (notificationsPuller.haveNewPosts()) {
                notificationsPuller.getNewPosts().forEach(newPosts -> newPosts.forEach(System.out::println));
            }

        }
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop(){
        working = false;
        notificationsPuller._stop();
    }
}
