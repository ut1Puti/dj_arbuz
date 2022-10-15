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
     * Поле для данных групп
     */
    private static GroupsStorage dataBase;
    /**
     * Поле для данных пользователей
     */
    private static UserStorage dataUsersBase;

    /**
     * Метод запускающий все сущности
     */
    public static void initInstances() {
        HttpServer server = HttpServer.getInstance();

        if (server == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        server.start();

        dataBase = GroupsStorage.getInstance();
        dataUsersBase = UserStorage.getInstance();
    }

    /**
     * Метод останавливающий все сущности
     */
    public static void stopInstances() {
        dataBase.saveToJsonFile();
        dataUsersBase.saveToJsonFile();
        HttpServer server = HttpServer.getInstance();
        server.stop();
    }
}
