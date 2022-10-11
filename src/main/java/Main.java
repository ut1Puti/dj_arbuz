import bots.consolebot.ConsoleBot;
import httpserver.server.HttpServer;

import database.Storage;

public class Main {
    public static void main(String[] args) {
        HttpServer server = HttpServer.getInstance();

        if (server == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        server.start();
        Storage dataBase = Storage.getInstance();
        ConsoleBot consoleBot = new ConsoleBot();
        consoleBot.run();
        dataBase.saveToJsonFile();
        server.stop();
    }
}