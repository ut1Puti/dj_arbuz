package dj.arbuz;

import dj.arbuz.bots.console.ConsoleBot;
import dj.arbuz.bots.telegram.TelegramBot;
import dj.arbuz.httpserver.server.HttpServer;
import dj.arbuz.database.UserStorage;

import dj.arbuz.database.GroupsStorage;
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
        TelegramBot telegramBot = new TelegramBot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        consoleBot.start();

        while (consoleBot.isWorking() || telegramBot.isWorking()) Thread.onSpinWait();

        dataBase.saveToJsonFile();
        dataUsersBase.saveToJsonFile();
        telegramBot.stopWithInterrupt();
        server.stop();
    }
}