package bots;

import handlers.Handler;
import handlers.HandlerResponse;
import user.User;

import java.util.Scanner;

/**
 * Класс консольного бота
 * @author Кедровских Олег
 * @version 0.9
 */
public class ConsoleBot {
    /** Поле показывающее работает ли бот */
    private boolean working;

    /**
     * Процедура получающая ответы от пользователя и отправляющая ответы.
     * Работает до тех пор пока пользователь не прекратит работу бота.
     * @param input Сканнер получаюший сообщения от пользователя
     */
    public void run(Scanner input){
        working = true;
        User user = null;
        while(working){
            HandlerResponse response = Handler.executeMessage(input.nextLine(), user, this);
            if (response.hasOutMessage()){
                System.out.println(response.getOutMessage());
            }
            if (response.hasCreateUser()){
                user = response.getCreateUser().createUser();
            }
        }
    }

    /**
     * Процедура прекращающая работу бота
     */
    public void stop(){
        working = false;
    }
}
