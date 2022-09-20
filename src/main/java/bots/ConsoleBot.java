package bots;

import handlers.Handler;

import java.util.Scanner;

public class ConsoleBot {
    boolean work = true;

    public void start(Scanner input){
        while(work){
            String message = input.nextLine();
            System.out.println(Handler.getMessage(message, this));
        }
    }

    public void stop(){
        work = false;
    }
}
