package bots;

import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.NotificationsPullingThread;
import user.User;

import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

/**
 * Класс консольного бота
 *
 * @author Кедровских Олег
 * @version 1.4
 */
public class ConsoleBot {
    /** Поле показывающее работает ли бот */
    private boolean working;
    /** Поле хранящее пользователя пользующегося ботом */
    private User currentUser = null;
    /** Поле класса получающего новые посты */
    private NotificationsPullingThread notificationsPuller = new NotificationsPullingThread();
    private SynchronousQueue<String> sq = new SynchronousQueue<>();
    private Thread t1 = new Thread(() -> {
        while (true) {
            if (notificationsPuller.haveNewPosts()) {
                notificationsPuller.getNewPosts().forEach(newPosts -> newPosts.forEach(System.out::println));
            }
        }
    });
    private Thread t2 = new Thread(() -> {
       Scanner s = new Scanner(System.in);
       while (true) {
           MessageHandlerResponse response = MessageHandler.executeMessage(s.nextLine(), currentUser, this);

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
    });

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     *
     * Работает до тех пор пока пользователь не прекратит работу бота.
     * @param input Сканнер получаюший сообщения от пользователя
     */
    public void run(Scanner input){
        working = true;
        t1.start();
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
