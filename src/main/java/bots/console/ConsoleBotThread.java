package bots.console;

public class ConsoleBotThread extends Thread {
    private ConsoleBot consoleBot = new ConsoleBot();

    @Override
    public void run() {
        consoleBot.run();
    }
}
