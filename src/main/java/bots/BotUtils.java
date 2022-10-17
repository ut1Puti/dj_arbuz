package bots;

import database.GroupsStorage;
import database.UserStorage;
import httpserver.server.HttpServer;

/**
 * Класс для управления данными и сервером необходимым при запуске бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class BotUtils {
    /**
     * Метод запускающий все сущности
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
