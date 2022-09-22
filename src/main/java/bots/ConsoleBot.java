package bots;

import handlers.Handler;
import handlers.HandlerVkApi;
import user.User;

import java.util.Scanner;

public class ConsoleBot {
    private boolean work = true;

    public void run(){
        System.out.println("Auth");
        User user = HandlerVkApi.initUser();
        while(work){
            //тут крч ошибка если много символов выводишь в консоль, хз как пофиксить
            //вылазит не всегда. Точно вылазит если пытаться вывети /help больше чем в одну строку
            //сделать это надо потому что он не помещается в одну видимую на экране строку
            System.out.println(Handler.executeMessage(new Scanner(System.in).nextLine(), user, this));
        }
    }

    public void stop(){
        work = false;
    }
}
