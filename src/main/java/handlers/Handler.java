package handlers;

import bots.ConsoleBot;
import com.vk.api.sdk.objects.groups.Group;
import user.User;

import java.util.Scanner;

public class Handler {
    public static String getMessages(Scanner input, ConsoleBot bot, User user){
        String message = input.next();
        if (message.equals("/help")){
            return "some help info";
        }
        else if (message.equals("/stop")) {
            bot.stop();
            return "stop";
        }
        else if (message.equals("/link")){
            Group group =  HandlerVkApi.getMusicianGroup(input.nextLine(), user);
            System.out.println(group);
            if (group == null){
                return "error:HandlerVkApi:getMusicianGroup";
            }
            if (HandlerVkApi.joinGroup(group.getId(), user)){
                System.out.println("Joined to group");
            }
            else {
                System.out.println("error:HandlerVkApi:joinGroup");
            }
            if (!HandlerVkApi.notificationTurn()){
                System.out.println("error:HandlervkApi:notificationTurn");
            }
            System.out.println("https://vk.com/" + group.getScreenName());
        }
        return "unknown command";
    }
}
