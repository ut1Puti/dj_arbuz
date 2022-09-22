package bots;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.Handler;
import handlers.HandlerVkApi;
import user.User;

import java.util.Scanner;

public class ConsoleBot {
    private boolean work = true;

    public void run(Scanner input){
        System.out.println("Auth");
        User user = HandlerVkApi.initUser(input);
        while(work){
            System.out.println(Handler.getMessages(input, this, user));
        }
    }

    public void stop(){
        work = false;
    }
}
