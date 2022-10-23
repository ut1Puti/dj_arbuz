package bots;

import database.GroupsStorage;
import database.UserStorage;
import handlers.vk.Vk;
import httpserver.server.HttpServer;

/**
 * Класс хранящий объекты необходимые для запуска бота
 *
 * @author Кедровских Олег
 * @version 0.3
 */
public class BotStarterPack {
    /**
     * Поле http сервера
     *
     * @see HttpServer
     */
    private final HttpServer httpServer;
    /**
     * Поле хранилища групп на которые оформленна подписка
     *
     * @see GroupsStorage
     */
    public final GroupsStorage groupsStorage;
    /**
     * Поле хранилища пользователей аутентифицированных через vk
     *
     * @see UserStorage
     */
    public final UserStorage userStorage;
    /**
     * Поле класса для взаимодействия с vk
     */
    public final Vk vk;

    /**
     * Конструктор - создает экземпляр класса
     */
    public BotStarterPack(String vkAppConfigurationJsonFilePath) {
        this.httpServer = HttpServer.getInstance();

        if (httpServer == null) {
            throw new RuntimeException("Не удалось настроить сервер");
        }

        httpServer.start();
        this.groupsStorage = GroupsStorage.getInstance();
        this.userStorage = UserStorage.getInstance();
        this.vk = new Vk(vkAppConfigurationJsonFilePath);
    }

    /**
     * Метод останавливающий и сохраняющий состояние объектов хранимых в {@code BotStarterPack}
     */
    public void stop() {
        httpServer.stop();
        groupsStorage.saveToJsonFile();
        userStorage.saveToJsonFile();
    }
}
