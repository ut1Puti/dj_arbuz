package handlers;

import bots.ConsoleBot;
import com.vk.api.sdk.objects.groups.Group;
import user.User;

public class Handler {
    private static final String helpInfo = "I'm bot helping interacting with music industry." +
                                            "I could find vk page of musician you would like, join you to his group." +
                                            "To get link to verified artist you use /link artist name or tag." +
                                            "For joining use /join artist name or tag." +
                                            "For stopping use /stop." +
                                            "You could display help info again with /help.";

    public static String executeMessage(String input, User user, ConsoleBot bot){
        String[] message = input.split(" ", 2);
        switch (message[0]) {
            case "/help" -> {
                return helpInfo;
            }
            case "/stop" -> {
                bot.stop();
                return "stopped";
            }
            case "/link" -> {
                Group group = HandlerVkApi.searchVerifiedGroup(message[1], user);
                if (group == null) {
                    return "Couldn't find verified group(";
                }
                return "https://vk.com/" + group.getScreenName();
            }
            case "/join" -> {
                if (HandlerVkApi.joinGroup(message[1], user)) {
                    return "Joined to group or you already a member";
                }
                return "Couldn't join group(";
            }
            case "/turn_on_notifications" -> {
                HandlerVkApi.turnNotifications(true, HandlerVkApi.searchGroups(message[1], user).get(0), user);
                return "not done";
            }
            case "/turn_off_notifications" -> {
                HandlerVkApi.turnNotifications(false, HandlerVkApi.searchGroups(message[1], user).get(0), user);
                return "not done";
            }
        }
        return "Unknown command. Use /help to get possible commands.";
    }
}
