package database;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс хранящий данные о группе в базе данных
 *
 * @author Кедровсикх Олег
 * @version 1.0
 */
public class GroupRelatedData {
    /**
     * Поле хранящее подписанных пользователей
     */
    private List<String> subscribedUsersId = new ArrayList<>();
    /**
     * Поле хранящее дату последнего поста
     */
    private int lastPostDate = 0;

    /**
     * Констуктор - создает экземпляр класса
     */
    public GroupRelatedData() {

    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param subscribedUsersId - подписанные пользователи
     */
    public GroupRelatedData(List<String> subscribedUsersId) {
        this.subscribedUsersId = subscribedUsersId;
    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param lastPostDate - дата последнего поста полученного в группе
     */
    public GroupRelatedData(int lastPostDate) {
        this.lastPostDate = lastPostDate;
    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param subscribedUsersId - подписанные пользователи
     * @param lastPostDate      - дата последнего поста полученного в группе
     */
    public GroupRelatedData(List<String> subscribedUsersId, int lastPostDate) {
        this.subscribedUsersId = subscribedUsersId;
        this.lastPostDate = lastPostDate;
    }

    /**
     * Метод возвращающий подписанных на группу пользователей
     *
     * @return список подписанных пользователей
     */
    public List<String> getSubscribedUsersId() {
        return subscribedUsersId;
    }

    /**
     * Метод получающий дату последнего поста
     *
     * @return дату последнего поста
     */
    public int getLastPostDate() {
        return lastPostDate;
    }

    /**
     * Метод добавляющий подписчика группы
     *
     * @param newSubscriberId - id нового подписчика
     */
    public void addNewSubscriber(String newSubscriberId) {
        subscribedUsersId.add(newSubscriberId);
    }

    /**
     * Метод добавляющий новых подписчиков
     *
     * @param newSubscribersId - список id новых подписчиков
     */
    public void addNewSubscribers(List<String> newSubscribersId) {
        subscribedUsersId.addAll(newSubscribersId);
    }

    /**
     * Метод обновляющий дату последнего поста
     *
     * @param newLastPostDate - новая дата последнего поста
     */
    public void updateLastPostDate(int newLastPostDate) {
        lastPostDate = newLastPostDate;
    }

    /**
     * Метод проверяющий подписан ли пользователь на эту группу
     *
     * @param subscriberId - id пользователя
     * @return true - если пользователь подписан
     * false - если пользователь не подписан
     */
    public boolean contains(String subscriberId) {
        return subscribedUsersId.contains(subscriberId);
    }
}
