package handlers;

import bots.ConsoleBot;

public class Handler {
    public static String getMessage(String message, ConsoleBot bot){
        if (message.equals("/help")){
            return "some help info";
        }
        else if (message.equals("/stop")) {
            bot.stop();
            return "stop";
        }
        else if (message.equals("/searchMusician")){
            return HandlerVkApi.getMusicianLink("some musician");
        }
        return "unknown command";
    }
}
