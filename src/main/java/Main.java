import bots.ConsoleBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import database.Storage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClientException, ApiException, URISyntaxException, IOException {
        Scanner input = new Scanner(System.in);
        Storage storage = new Storage();
        new ConsoleBot().run(input);
        input.close();
    }
}
