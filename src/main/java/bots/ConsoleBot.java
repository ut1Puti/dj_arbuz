package bots;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.Handler;
import handlers.HandlerVkApi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ConsoleBot {
    private boolean work = true;

    public void run(Scanner input) throws ClientException, ApiException, URISyntaxException, IOException {
        while (!HandlerVkApi.initUser()){
            System.out.println("Auth");
        }
        while(work){
            String message = input.nextLine();
            System.out.println(Handler.getMessage(message, this));
        }
    }

    public void stop(){
        work = false;
    }
}
