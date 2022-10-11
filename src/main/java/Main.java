import bots.ConsoleBot;
import bots.TelegramBot;
import com.vk.api.sdk.actions.Storage;
import database.UserStorage;
import httpserver.HttpServer;

import java.io.IOException;
import java.util.Scanner;

import database.GroupsStorage;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import user.User;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.getInstance();
        GroupsStorage dataBase = GroupsStorage.storageGetInstance();
        UserStorage dataUsersBase = UserStorage.storageGetInstance();
        Scanner input = new Scanner(System.in);
        //new ConsoleBot().run(input)
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        dataBase.saveToJsonFile();
        dataUsersBase.saveToJsonFile();
        server.stop();
        input.close();
    }
}