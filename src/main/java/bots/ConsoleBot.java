package bots;

import database.UserStorage;
import handlers.messages.MessageHandler;
import handlers.messages.HandlerResponse;
import handlers.messages.TextResponse;
import user.User;

import java.util.Scanner;

/**
 * Класс консольного бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ConsoleBot {
    /**
     * Поле показывающее работает ли бот
     */
    private boolean working;

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     * Работает до тех пор, пока пользователь не прекратит работу бота.
     *
     * @param input Сканнер получаюший сообщения от пользователя
     */
    public void run(Scanner input) {
        working = true;
        User user = null;
        while (working) {
            HandlerResponse response = MessageHandler.executeMessage(input.nextLine(), "-1", this);

            if (response.hasTextMessage()) {
                System.out.println(response.getTextMessage());
            }

            if (response.hasUpdateUser()) {
                user = response.getUpdateUser()
                               .createUser("-1");
                if (user == null) {
                    System.out.println(TextResponse.AUTH_ERROR);
                }
                if (user != null) {
                    UserStorage userBase = UserStorage.storageGetInstance();
                    userBase.addInfoUser("-1", user);
                }
            }

        }
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop() {
        working = false;
    }
}
