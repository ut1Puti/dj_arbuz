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
 */
public abstract class PostsPullingThread extends StoppableThread {
    /**
     * Поле хранилища групп
     */
    protected final GroupsStorage groupsBase = GroupsStorage.getInstance();
    /**
     * Поле обработчика обращений к vk api
     */
    protected Vk vk = new Vk("src/main/resources/anonsrc/vkconfig.properties");

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
