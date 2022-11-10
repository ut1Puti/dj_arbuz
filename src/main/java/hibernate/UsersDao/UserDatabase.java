package hibernate.UsersDao;

import hibernate.entity.UserData;
import user.BotUser;

public class UserDatabase {
    private final UserDao userDao = new UserDao();

    public boolean addInfoUser(String userId, BotUser userData) {
        UserData tempUser = new UserData();
        tempUser.setTelegramId(userId);
        tempUser.setUserId(userData.getId());
        tempUser.setAccessToken(userData.getAccessToken());
        userDao.updateUser(tempUser);
        return true;
    }

    public boolean contains(String user) {
        return userDao.getUser(user) != null;
    }

    public BotUser getUser(String user) {
        return new BotUser(userDao.getUser(user));
    }

}
