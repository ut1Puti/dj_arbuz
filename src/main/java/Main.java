import bots.ConsoleBot;
import database.Storage;
import httpserver.HttpServer;
import java.io.IOException;
import java.util.Scanner;
import database.GroupsStorage;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.getInstance();
        Storage dataBase = GroupsStorage.storageGetInstance();
        Scanner input = new Scanner(System.in);
        new ConsoleBot().run(input);
        dataBase.saveToJsonFile();
        server.stop();
        input.close();
    }
}