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
    private volatile boolean working;
    /** Поле хранящее пользователя пользующегося ботом */
    private User currentUser = null;
    /** Поле класса получающего новые посты */
    private final NotificationsPullingThread notificationsPuller = new NotificationsPullingThread();
    /** Поле очереди хранящий не отправленные сообщения бота */
    private final SynchronousQueue<String> botMessagesQueue = new SynchronousQueue<>();
    /** Поле потока получающего новые посты */
    private final Thread notificationsPullerThread = new Thread(() -> {
        while (working) {
            if (notificationsPuller.haveNewPosts()) {
                notificationsPuller.getNewPosts().forEach(newPosts -> {
                    for (String newPost : newPosts) {
                        try {
                            botMessagesQueue.put(newPost);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
    });
    /** Поле потока ввода данных пользователя и обработки команды */
    private final Thread userInputThread = new Thread(() -> {
       Scanner userInput = new Scanner(System.in);
       while (working) {
           MessageHandlerResponse response = MessageHandler.executeMessage(userInput.nextLine(), currentUser, this);

           if (response.hasTextMessage()) {
               try {
                   botMessagesQueue.put(response.getTextMessage());
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }

           if (response.hasUpdateUser()) {
               currentUser = response.getUpdateUser().createUser();

               if (currentUser == null) {
                   try {
                       botMessagesQueue.put(BotTextResponse.AUTH_ERROR);
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               }

           }
       }
       userInput.close();
    });
    /** Поле потока вывода сообщений пользователю */
    private final Thread printThread = new Thread(() -> {
        while (working) {
            try {
                String s = botMessagesQueue.take();
                System.out.println(s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     * Работает до тех пор пока пользователь не прекратит работу бота.
     */
    public void run(){
        working = true;
        notificationsPullerThread.start();
        userInputThread.start();
        printThread.start();
        while (working) Thread.onSpinWait();
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop(){
        working = false;
        notificationsPuller._stop();
    }
}
