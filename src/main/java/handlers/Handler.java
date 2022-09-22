package handlers;

import bots.ConsoleBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import user.User;

import java.util.Scanner;

public class Handler {
    public static String getMessage(String message, ConsoleBot bot, User user){
        if (message.equals("/help")){
            return "some help info";
        }
        else if (message.equals("/stop")) {
            bot.stop();
            return "stop";
        }
        else if (message.equals("/link")){
            Group group =  HandlerVkApi.getMusicianGroup("lida", user);
            System.out.println(group);
            if (group == null){
                return "error: HandlerVkApi: getMusicianGroup";
            }
            if (HandlerVkApi.joinGroup(group.getId(), user)){
                System.out.println("Joined to group");
            }
            System.out.println("https://vk.com/" + group.getScreenName());
        }
        return "unknown command";
    }
}
