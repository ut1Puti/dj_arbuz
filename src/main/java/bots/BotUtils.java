package bots;

import database.GroupsStorage;
import database.UserStorage;
import httpserver.server.HttpServer;

/**
 * Класс для управления сущностями необходимым при запуске бота
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see HttpServer
 * @see GroupsStorage
 * @see UserStorage
 */
public class BotUtils {
    /**
     * Метод запускающий все сущности, необходимые для запуска бота
     *
     * @see HttpServer#getInstance()
     * @see GroupsStorage#getInstance()
     * @see UserStorage#getInstance()
     */
    public static void initInstances() {
        HttpServer server = HttpServer.getInstance();

        if (server == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        server.start();

        GroupsStorage dataBase = GroupsStorage.getInstance();
        UserStorage dataUsersBase = UserStorage.getInstance();
    }

    /**
     * Метод останавливающий все сущности
     *
     * @see HttpServer#getInstance()
     * @see HttpServer#stop()
     * @see GroupsStorage#getInstance()
     * @see GroupsStorage#saveToJsonFile()
     * @see UserStorage#getInstance()
     * @see UserStorage#saveToJsonFile()
     */
    public static void stopInstances() {
        GroupsStorage dataBase = GroupsStorage.getInstance();
        UserStorage dataUsersBase = UserStorage.getInstance();
        dataBase.saveToJsonFile();
        dataUsersBase.saveToJsonFile();
        HttpServer server = HttpServer.getInstance();
        server.stop();
    }
}
