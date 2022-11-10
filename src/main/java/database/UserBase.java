package database;

import user.BotUser;

public interface UserBase {
    boolean addInfoUser(String user, BotUser userData);

    boolean contains(String user);

    BotUser getUser(String user);
}
