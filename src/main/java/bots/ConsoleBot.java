package bots;

import handlers.Handler;
import handlers.HandlerVkApi;
import user.User;

import java.util.Scanner;

public class ConsoleBot {
    private boolean work = false;

    public void start(Scanner input){
        User user = null;
        while (user == null){
            user = Handler.executeStartMessage(input.nextLine(), input);
        }
        System.out.println("Auth completed");
        work = true;
        run(input, user);
    }

    public void stop(){
        work = false;
    }

    public void run(Scanner input, User user){
        while(work){
            System.out.println(Handler.executeMessage(input.nextLine(), user, this));
        }
    }
}
