package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс хранящий данные о группе в базе данных
 *
 * @author Кедровских Олег
 * @version 1.5
 */
class GroupRelatedData {
    /**
     * Поле хранящее подписанных пользователей
     */
    private final List<String> subscribedUsersId;
    /**
     * Поле хранящее дату последнего поста
     */
    private final long lastPostDate;

    /**
     * Конструктор - создает экземпляр класса
     */
    GroupRelatedData() {
        this.subscribedUsersId = new ArrayList<>();
        this.lastPostDate = 0;
    }

    /**
     * Констуктор - создает экземпляр класса
     *
     * @param subscribedUsersId - подписанные пользователи
     */
    GroupRelatedData(List<String> subscribedUsersId) {
        this.subscribedUsersId = subscribedUsersId;
        this.lastPostDate = 0;
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

    private List<String> copySubscribersId() {
        return subscribedUsersId.stream().toList();
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
    GroupRelatedData updateLastPostDate(long newLastPostDate) {
        return new GroupRelatedData(this.subscribedUsersId, newLastPostDate);
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

    /**
     * Метод вычисляющий хэш экземляра класса
     *
     * @return хэш экземпляра
     */
    @Override
    public int hashCode() {
        return subscribedUsersId.hashCode();
    }

    /**
     * Метод проверяющий равенство {@code obj} и {@code GroupRelatedData}
     *
     * @param obj - сравниваемый объект
     * @return true если объекты равны по полям, false если объекты не равны по полям
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof GroupRelatedData anotherGroupRelatedData)) {
            return false;
        }

        return Objects.equals(subscribedUsersId, anotherGroupRelatedData.subscribedUsersId);
    }
}
