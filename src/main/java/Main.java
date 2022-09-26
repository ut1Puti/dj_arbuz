import bots.ConsoleBot;
import database.Storage;
import httpserver.HttpServer;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.getInstance();
        Scanner input = new Scanner(System.in);
        new ConsoleBot().run(input);
        server.stop();
        input.close();
    }
}
