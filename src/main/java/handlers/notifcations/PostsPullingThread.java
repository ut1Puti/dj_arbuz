package handlers.notifcations;

import database.GroupsStorage;
import socialnetworks.socialnetwork.SocialNetwork;
import stoppable.StoppableThread;

import java.util.List;

/**
 * Абстрактный класс потока получающего новые посты. Поток является потоком демоном
 *
 * @author Кедровских Олег
 * @version 1.3
 * @see StoppableThread
 */
public abstract class PostsPullingThread extends StoppableThread {
    /**
     * Поле хранилища групп
     *
     * @see GroupsStorage
     */
    protected final GroupsStorage groupsBase;
    /**
     * Поле обработчика обращений к vk api
     *
     * @see SocialNetwork
     */
    protected final SocialNetwork socialNetwork;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param groupsStorage база данных групп на которые оформлена подписка
     * @param socialNetwork интерфейс реализующий доступ к необходимым методам социальной сети
     */
    protected PostsPullingThread(GroupsStorage groupsStorage, SocialNetwork socialNetwork) {
        this.groupsBase = groupsStorage;
        this.socialNetwork = socialNetwork;
        this.setDaemon(true);
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @return есть или нет новые посты
     */
    public abstract boolean hasNewPosts();

    /**
     * Метод получающий новые посты
     *
     * @return список новых постов
     */
    public abstract List<String> getNewPosts();
}
