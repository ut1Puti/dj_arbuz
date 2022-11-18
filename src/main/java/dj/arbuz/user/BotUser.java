package dj.arbuz.user;

import com.vk.api.sdk.client.actors.UserActor;
import dj.arbuz.database.hibernate.entity.UserData;

/**
 * Класс пользователя
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see UserActor
 */
public final class BotUser extends UserActor {
    /**
     * Поле id пользователя в телеграме
     */
    private final String telegramId;

    /**
     * Конструктор от унаследованного класса
     *
     * @param vkId        - id пользователя в vk
     * @param accessToken - токен для доступа к vk api
     * @param telegramId  - id пользователя в телеграме
     */
    public BotUser(Integer vkId, String accessToken, String telegramId) {
        super(vkId, accessToken);
        this.telegramId = telegramId;
    }
    public BotUser(UserData userData) {
        super(userData.getUserId(), userData.getAccessToken());
        this.telegramId = userData.getTelegramId();
    }

    /**
     * Метод для получения {@code telegramId}
     *
     * @return id пользователя в телеграмме
     */
    public String getTelegramId() {
        return telegramId;
    }

    /**
     * Метод вычисляющий хэш экземляра класса
     *
     * @return хэш экземпляра
     */
    @Override
    public int hashCode() {
        return super.hashCode() + telegramId.hashCode();
    }

    /**
     * Метод проверяющий равенство {@code obj} и {@code User}
     *
     * @param obj - сравниваемый объект
     * @return true если объекты равны по полям, false если объекты не равны по полям
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BotUser)) {
            return false;
        }

        return super.equals(obj) && telegramId.equals(((BotUser) obj).telegramId);
    }
}
