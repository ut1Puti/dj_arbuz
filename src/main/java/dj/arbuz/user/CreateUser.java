package dj.arbuz.user;

/**
 * Интерфейс позволяющий переопределить каким образом будут получаться переменные для создания пользователя
 *
 * @author Кедровских Олег
 * @version 1.1
 */
@FunctionalInterface
public interface CreateUser {
    User createUser(String telegramId);
}
