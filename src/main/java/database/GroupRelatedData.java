package database;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс хранящий данные о группе в базе данных
 *
 * @author Кедровсикх Олег
 * @version 1.0
 */
class GroupRelatedData {
    /**
     * Поле хранящее подписанных пользователей
     */
    private List<String> subscribedUsersId;
    /**
     * Поле хранящее дату последнего поста
     */
    private long lastPostDate = 0;

    /**
     * Констуктор - создает экземпляр класса
     */
    GroupRelatedData() {

    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param subscribedUsersId - подписанные пользователи
     */
    GroupRelatedData(List<String> subscribedUsersId) {
        this.subscribedUsersId = subscribedUsersId;
    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param lastPostDate - дата последнего поста полученного в группе
     */
    GroupRelatedData(long lastPostDate) {
        this.subscribedUsersId = new ArrayList<>();
        this.lastPostDate = lastPostDate;
    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param subscribedUsersId - подписанные пользователи
     * @param lastPostDate      - дата последнего поста полученного в группе
     */
    GroupRelatedData(List<String> subscribedUsersId, long lastPostDate) {
        this.subscribedUsersId = subscribedUsersId;
        this.lastPostDate = lastPostDate;
    }

    /**
     * Метод возвращающий подписанных на группу пользователей
     *
     * @return список подписанных пользователей
     */
    List<String> getSubscribedUsersId() {
        return subscribedUsersId;
    }

    /**
     * Метод получающий дату последнего поста
     *
     * @return дату последнего поста
     */
    long getLastPostDate() {
        return lastPostDate;
    }

    /**
     * Метод добавляющий подписчика группы
     *
     * @param newSubscriberId - id нового подписчика
     */
    void addNewSubscriber(String newSubscriberId) {
        if (subscribedUsersId.contains(newSubscriberId)) {
            return;
        }

        subscribedUsersId.add(newSubscriberId);
    }

    /**
     * Метод добавляющий новых подписчиков
     *
     * @param newSubscribersId - список id новых подписчиков
     * @see GroupRelatedData#addNewSubscriber(String)
     */
    void addNewSubscribers(List<String> newSubscribersId) {
        newSubscribersId.forEach(this::addNewSubscriber);
    }

    /**
     * Метод обновляющий дату последнего поста
     *
     * @param newLastPostDate - новая дата последнего поста
     */
    void updateLastPostDate(long newLastPostDate) {
        this.lastPostDate = newLastPostDate;
    }

    /**
     * Метод проверяющий подписан ли пользователь на эту группу
     *
     * @param subscriberId - id пользователя
     * @return true - если пользователь подписан
     * false - если пользователь не подписан
     */
    boolean contains(String subscriberId) {
        return subscribedUsersId.contains(subscriberId);
    }
}
