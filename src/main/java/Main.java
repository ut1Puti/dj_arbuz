import bots.ConsoleBot;
import httpserver.HttpServer;
import java.io.IOException;
import java.util.Scanner;
import database.Storage;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.getInstance();
        Scanner input = new Scanner(System.in);
        new ConsoleBot().run(input);
        server.stop();
        input.close();
    }
}
