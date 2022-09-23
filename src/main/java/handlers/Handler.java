package handlers;

import bots.ConsoleBot;
import com.vk.api.sdk.objects.groups.Group;
import user.User;

import java.util.Scanner;

public class Handler {
    private static final String helpInfo = "I'm bot helping interacting with music industry\n" +
                                            "I could find vk page of musician you would like, join you to his group\n" +
                                            "To get link to verified artist you use /link artist name or tag\n" +
                                            "To get group id use /id artist name or tag\n" +
                                            "For joining use /join artist name or tag\n" +
                                            "For stopping use /stop\n" +
                                            "You could display help info again with /help";

    public static User executeStartMessage(String message, Scanner input){
        if (message.equals("/start")){
            System.out.println("Auth. Enter opened in browser link:");
            return HandlerVkApi.initUser(input);
        }
        System.out.println("Enter /start to start bot");
        return null;
    }

    public static String executeMessage(String message, User user, ConsoleBot bot){
        String[] commandAndArg = message.split(" ", 2);
        switch (commandAndArg[0]) {
            case "/help" -> {
                return helpInfo;
            }
            case "/stop" -> {
                bot.stop();
                return "stopped";
            }
            case "/link" -> {
                Group group = HandlerVkApi.searchVerifiedGroup(commandAndArg[1], user);
                if (group == null) {
                    return "Couldn't find verified group(";
                }
                return "https://vk.com/" + group.getScreenName();
            }
            case "/id" -> {
                Group group = HandlerVkApi.searchVerifiedGroup(commandAndArg[1], user);
                if (group == null) {
                    return "Couldn't find verified group(";
                }
                return String.valueOf(group.getId());
            }
            case "/join" -> {
                if (HandlerVkApi.joinGroup(commandAndArg[1], user)) {
                    return "Joined to group or you already a member";
                }
                return "Couldn't join or find group(";
            }
            case "/turn_on_notifications" -> {
                HandlerVkApi.turnNotifications(true, HandlerVkApi.searchVerifiedGroup(commandAndArg[1], user), user);
                return "not done";
            }
            case "/turn_off_notifications" -> {
                HandlerVkApi.turnNotifications(false, HandlerVkApi.searchVerifiedGroup(commandAndArg[1], user), user);
                return "not done";
            }
        }
        return "Unknown command. Use /help to get possible commands.";
    }
}
