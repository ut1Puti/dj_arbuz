package handlers.notifcations;

import database.GroupsStorage;
import handlers.vk.Vk;
import stoppable.StoppableThread;

import java.util.List;

/**
 * Абстрактный класс получающий новые посты
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
     * @see Vk
     */
    protected final Vk vk;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param groupsStorage база данных групп на которые оформленна подписка
     */
    public PostsPullingThread(GroupsStorage groupsStorage, Vk vk) {
        this.groupsBase = groupsStorage;
        this.vk = vk;
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
