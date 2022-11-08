package user;

/**
 * Интерфейс позволяющий переопределить каким образом будут получаться переменные для создания пользователя
 *
 * @author Кедровских Олег
 * @version 1.1
 */
@FunctionalInterface
public interface CreateUser {
    /**
     * Метод создающий пользователя, имеющего id в системе
     *
     * @param systemUserId - id пользователя в системе
     * @return пользователя содержащего id в vk и в системе,
     * token - ключ доступа к vk api с использованием аккаунта пользоватлеля
     */
    User createUser(String systemUserId);
}
