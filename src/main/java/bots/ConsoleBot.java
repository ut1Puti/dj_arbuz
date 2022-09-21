package bots;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.Handler;
import handlers.HandlerVkApi;
import user.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ConsoleBot {
    private boolean work = true;

    public void run(Scanner input) throws ClientException, ApiException, URISyntaxException, IOException {
        User user;
        do {
            System.out.println("Auth");
        } while ((user = HandlerVkApi.initUser()) == null);
        while(work){
            String message = input.next();
            System.out.println(Handler.getMessage(message, this, user));
        }
    }

    public void stop(){
        work = false;
    }
}
