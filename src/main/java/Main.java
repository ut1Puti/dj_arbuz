import bots.ConsoleBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClientException, ApiException, URISyntaxException, IOException {
        new ConsoleBot().run(new Scanner(System.in));
    }
}
