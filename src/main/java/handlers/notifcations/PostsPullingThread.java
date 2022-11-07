package handlers.notifcations;

import database.GroupsStorage;
import socialnetworks.vk.Vk;
import stoppable.StoppableThread;

import java.util.List;

/**
 * Абстрактный класс потока получающего новые посты. Поток является потоком демоном
 *
 * @author Кедровских Олег
 * @version 2.0
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
     * @see Vk
     */
    protected final Vk socialNetwork;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param groupsStorage база данных групп на которые оформлена подписка
     * @param vk            класс реализующий доступ к методам обработчика запросов к социальной сети
     */
    protected PostsPullingThread(GroupsStorage groupsStorage, Vk vk) {
        this.groupsBase = groupsStorage;
        this.socialNetwork = vk;
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
