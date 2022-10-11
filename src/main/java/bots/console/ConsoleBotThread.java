package bots.console;

import bots.BotTextResponse;
import database.UserStorage;
import handlers.messages.MessageHandler;
import handlers.messages.MessageHandlerResponse;
import handlers.notifcations.Notifications;
import user.User;

import java.util.Scanner;

/**
 * Класс потока обрабатывающего сообщения пользователя с консоли
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ConsoleBotThread extends Thread {
    /**
     * Поле хранящее пользователя пользующегося ботом
     */
    private UserStorage userBase = UserStorage.getInstance();
    /**
     * Поле класса получающего новые посты
     */
    private final Notifications notifications = new Notifications();
    /**
     * Поле id пользователя консольной версии бота
     */
    private final String defaultConsoleUserId = "-10;";

    /**
     * Метод с логикой выполняемой внутри потока
     */
    @Override
    public void run() {
        notifications.start();
        Scanner userInput = new Scanner(System.in);
        while (!Thread.interrupted()) {

            if (userInput.hasNextLine()) {
                MessageHandlerResponse response = MessageHandler.executeMessage(
                        userInput.nextLine(), defaultConsoleUserId, this
                );

                if (response.hasTextMessage()) {
                    System.out.println(response.getTextMessage());
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

            if (notifications.hasNewPosts()) {
                notifications.getNewPosts().forEach(newPosts -> newPosts.forEach(System.out::println));
            }

        }
        userInput.close();
        notifications.stop();
    }
}
