package handlers.notifcations;

import database.GroupsStorage;
import handlers.vk.Vk;
import stoppable.StoppableThread;

import java.util.List;

/**
 * Абстрактный класс получающий новые посты
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see StoppableThread
 */
public abstract class PostsPullingThread extends StoppableThread {
    /**
     * Поле хранилища групп
     *
     * @see GroupsStorage
     */
    protected final GroupsStorage groupsBase = GroupsStorage.getInstance();
    /**
     * Поле обработчика обращений к vk api
     *
     * @see Vk
     */
    protected Vk vk = new Vk("src/main/resources/anonsrc/vk_config.json");

    /**
     * Метод проверяющий наличие новых постов
     *
     * @return есть или нет новые посты
     */
    public abstract boolean hasNewPosts();

    /**
     * Метод получающий новые посты
     *
     * @return новые посты
     */
    public abstract List<String> getNewPosts();
}
