import bots.ConsoleBot;
import database.Storage;

public class Main {
    public static void main(String[] args){
        Storage storage = new Storage();
        new ConsoleBot().run();
    }
}
