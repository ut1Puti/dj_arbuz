package user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс тестирующий класс пользователей бота
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class UserTests {
    /**
     * Поле id тестового пользователя
     */
    private final int userId = 100;
    /**
     * Поле токена доступа тестового пользователя
     */
    private final String accessToken = "accessToken";
    /**
     * Поле telegram id тестового пользователя
     */
    private final String userTelegramId = "telegramId";
    /**
     * Поле тестового пользователя
     */
    private final User user = new User(userId, accessToken, userTelegramId);

    /**
     * Метод тестирующий получение telegram id пользователя
     */
    @Test
    public void testGetTelegramId() {
        assertEquals(userTelegramId, user.getTelegramId());
    }

    /**
     * Метод тестирующий получение токена доступа пользователя
     */
    @Test
    public void testGetAccessToken() {
        assertEquals(accessToken, user.getAccessToken());
    }

    /**
     * Метод тестирующий получение id пользователя в vk
     */
    @Test
    public void testGetUserId() {
        assertEquals(userId, user.getId());
    }
}
