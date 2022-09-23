import bots.ConsoleBot;
import database.Storage;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        Storage storage = new Storage();
        new ConsoleBot().start(s);
        s.close();
    }
}
