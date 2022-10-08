package user;

/**
 * Интерфейс позволяющий переопределить каким образом будут получаться переменные для создания пользователя
 * @author Кедровских Олег
 * @version 1.0
 */
@FunctionalInterface
public interface CreateUser {
    public User createUser(String telegramUserId);
}
