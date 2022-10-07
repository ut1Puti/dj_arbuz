import bots.consolebot.ConsoleBot;
import httpserver.HttpServer;
import java.io.IOException;

import database.Storage;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.getInstance();
        Storage dataBase = Storage.getInstance();
        ConsoleBot consoleBot = new ConsoleBot();
        consoleBot.run();
        dataBase.saveToJsonFile();
        server.stop();
    }
}