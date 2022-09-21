package handlers;

import bots.ConsoleBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import user.User;

import java.util.Scanner;

public class Handler {
    public static String getMessage(String message, ConsoleBot bot, User user) throws ClientException, ApiException {
        if (message.equals("/help")){
            return "some help info";
        }
        else if (message.equals("/stop")) {
            bot.stop();
            return "stop";
        }
        else if (message.equals("/link")){
            return HandlerVkApi.getMusicianLink("lida", user);
        }
        return "unknown command";
    }
}
