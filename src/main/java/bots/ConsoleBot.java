package bots;

import handlers.MessageHandler;
import handlers.HandlerResponse;
import handlers.TextResponse;
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

    /**
     * Метод получающий ответы от пользователя и отправляющая ответы.
     * Работает до тех пор пока пользователь не прекратит работу бота.
     * @param input Сканнер получаюший сообщения от пользователя
     */
    public void run(Scanner input){
        working = true;
        User user = null;
        while(working){
            HandlerResponse response = MessageHandler.executeMessage(input.nextLine(), user, this);
            if (response.hasTextMessage()){
                System.out.println(response.getTextMessage());
            }
            if (response.hasCreateUser()){
                user = response.getUpdateUser().createUser();
                if (user == null){
                    System.out.println(TextResponse.AUTH_ERROR);
                }
            }
        }
    }

    /**
     * Метод прекращающая работу бота
     */
    public void stop(){
        working = false;
    }
}
