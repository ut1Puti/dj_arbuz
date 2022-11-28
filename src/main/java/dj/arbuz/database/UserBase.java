package dj.arbuz.database;

import dj.arbuz.user.BotUser;

import java.util.List;

/**
 * Интерфейс базы данных пользователей
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface UserBase {
    /**
     * Метод добавляющий информацию о новых пользователях
     *
     * @param userId id пользователя
     * @param botUser экземпляр класса пользователя
     * @return {@code true} - если пользователь был успешно добавлен,
     * {@code false} - если пользователь не был добавлен
     */
    boolean addUser(String userId, BotUser botUser);

    /**
     * Метод проверяющий наличие пользователя в базе по id
     *
     * @param userId id пользователя
     * @return {@code true} - если пользователь с таким id есть в базе, {@code false} - если пользователя нет в базе
     */
    boolean contains(String userId);

    /**
     * Метод получающий пользователя по id
     *
     * @param userId id пользователя
     * @return экземпляр класса пользователя по id
     */
    BotUser getUser(String userId);

    /**
     * Метод получающий id все пользователей пользующихся ботом
     *
     * @return список id все пользователей
     */
    List<String> getAllUsersId();
}
