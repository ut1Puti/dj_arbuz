import bots.console.ConsoleBot;
import bots.telegram.TelegramBot;
import httpserver.server.HttpServer;
import database.UserStorage;

import database.GroupsStorage;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        HttpServer server = HttpServer.getInstance();

        if (server == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        server.start();

        GroupsStorage dataBase = GroupsStorage.getInstance();
        UserStorage dataUsersBase = UserStorage.getInstance();

        ConsoleBot consoleBot = new ConsoleBot();
        DefaultBotSession defaultBotSession = new DefaultBotSession();
        TelegramBot telegramBot = new TelegramBot(defaultBotSession);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(defaultBotSession.getClass());
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        consoleBot.start();

        while (consoleBot.isWorking()) Thread.onSpinWait();

        dataBase.saveToJsonFile();
        dataUsersBase.saveToJsonFile();
        defaultBotSession.stop();
        server.stop();
    }
}