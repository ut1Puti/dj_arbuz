package hibernate.UsersDao;

import database.UserBase;
import hibernate.entity.UserData;
import user.BotUser;

/**
 * Класс для работы с таблицей юзеров в бд
 */
public class UserDatabase implements UserBase {
    /** Репозиторий для взаимодействия с таблицей юзеров **/
    private final UserDao userDao = new UserDao();

    /**
     * Метод для добавления новой информации юзера
     * @param userId Айди юзера
     * @param userData Данные юзера
     */
    public boolean addInfoUser(String userId, BotUser userData) {
        UserData tempUser = new UserData();
        tempUser.setTelegramId(userId);
        tempUser.setUserId(userData.getId());
        tempUser.setAccessToken(userData.getAccessToken());
        userDao.updateUser(tempUser);
        return true;
    }

    /**
     * Метод для проверки наличия юзера в бд
     */
    public boolean contains(String user) {
        return userDao.getUser(user) != null;
    }

    /**
     * Метод для получения юзера
     * @param user айди юзера в телеграме
     * @return Entity класс юзера взятый из бд
     */
    public BotUser getUser(String user) {
        return new BotUser(userDao.getUser(user));
    }

}
