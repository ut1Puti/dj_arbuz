import bots.console.ConsoleBotThread;
import bots.telegram.TelegramBotThread;
import httpserver.server.HttpServer;
import bots.telegram.TelegramBot;
import database.UserStorage;

import database.GroupsStorage;
import org.checkerframework.checker.units.qual.C;
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
        //ConsoleBot consoleBot = new ConsoleBot();
        //consoleBot.run();
        GroupsStorage dataBase = GroupsStorage.getInstance();
        UserStorage dataUsersBase = UserStorage.getInstance();
        ConsoleBotThread cth = new ConsoleBotThread();
        TelegramBotThread tgth = new TelegramBotThread();
        tgth.start();
        cth.start();
        try {
            tgth.join();
            cth.join();
        } catch (InterruptedException ignored) {}
        dataBase.saveToJsonFile();
        dataUsersBase.saveToJsonFile();
        server.stop();
    }
}