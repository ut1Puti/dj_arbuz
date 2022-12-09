package dj.arbuz.console;

import dj.arbuz.ExecutableMessage;
import httpserver.DefaultServerConfigPath;
import httpserver.HttpServer;
import stoppable.StoppableThread;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Класс потока обрабатывающего сообщения пользователя с консоли
 *
 * @author Кедровских Олег
 * @version 1.2
 * @see StoppableThread
 */
public final class ConsoleBot extends StoppableThread implements ExecutableMessage {
    /**
     * Поле id пользователя консольной версии бота
     */
    private static final String defaultConsoleUserId = "consoleUser";
    /**
     * Поле класса исполняющего команды полученные от пользователя
     *
     * @see ConsoleMessageExecutor
     */
    private final ConsoleMessageExecutor messageExecutor;

    /**
     * Конструктор - создает экземпляр класса
     */
    public ConsoleBot() {
        super(defaultConsoleUserId);
        messageExecutor = new ConsoleMessageExecutor(this);
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.createInstance(DefaultServerConfigPath.DEFAULT_SERVER_CONFIG_PATH);
        int startTimeout = 0;
        boolean isDaemon = true;
        httpServer.start(startTimeout, isDaemon);
        ConsoleBot consoleBot = new ConsoleBot();
        consoleBot.start();
        while (consoleBot.isWorking()) {
            Thread.onSpinWait();
        }
        consoleBot.stopWithInterrupt();
        httpServer.stop();
    }

    /**
     * Метод с логикой выполняемой внутри потока
     *
     * @see StoppableThread#run()
     * @see dj.arbuz.handlers.messages.MessageHandlerImpl#handleMessage(String, String)
     */
    @Override
    public void run() {
        Scanner userInput = new Scanner(System.in);
        while (working.get()) {

            if (userInput.hasNextLine()) {
                messageExecutor.executeTextMessage(defaultConsoleUserId, userInput.nextLine());
            }

        }
        working.set(false);
        userInput.close();
        messageExecutor.stop();
    }

    /**
     * Реализация интерфейса для отправки сообщения пользователю, выводит сообщение в консоль
     *
     * @param userSendResponseId  id пользователя, которому необходимо отправить сообщение
     * @param responseSendMessage сообщение, которое будет отправлено пользователю
     */
    @Override
    public void send(String userSendResponseId, String responseSendMessage) {

        if (!userSendResponseId.equals(defaultConsoleUserId)) {
            return;
        }

        System.out.println(responseSendMessage);
    }
}
