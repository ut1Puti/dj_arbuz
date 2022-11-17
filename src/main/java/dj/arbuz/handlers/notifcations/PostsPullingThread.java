package dj.arbuz.handlers.notifcations;

import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.database.GroupBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.vk.Vk;
import dj.arbuz.socialnetworks.vk.wall.VkPostsParser;
import stoppable.StoppableThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * @see GroupBase
     */
    protected final GroupBase groupsBase;
    /**
     * Поле обработчика обращений к vk api
     *
     * @see Vk
     */
    protected final Vk vk;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param groupsStorage база данных групп на которые оформлена подписка
     * @param vk            класс реализующий доступ к методам обработчика запросов к социальной сети
     */
    protected PostsPullingThread(GroupBase groupsStorage, Vk vk) {
        this.groupsBase = groupsStorage;
        this.vk = vk;
        this.setDaemon(true);
    }

    /**
     * Метод получающий новые посты из vk в виде строк
     *
     * @param groupScreenName название группы из базы данных
     * @return {@code Optional.empty()} если не нашлось новых постов или группа отсутствует в базе данных,
     * иначе возвращает {@code Optional.of(newPosts)}
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    protected final Optional<List<String>> getNewPostsAsStrings(String groupScreenName) throws SocialNetworkException {
        Optional<Long> optionalLastPostDate = groupsBase.getGroupLastPostDate(groupScreenName);

        if (optionalLastPostDate.isEmpty()) {
            return Optional.empty();
        }

        long lastPostDate = optionalLastPostDate.get();
        long newLastPostDate = lastPostDate;
        final int maxAmountOfPosts = 100;
        List<WallpostFull> appFindPosts = new ArrayList<>();
        for (WallpostFull appFindPost : vk.getLastPostAsPostsUnsafe(groupScreenName, maxAmountOfPosts)) {
            int appFindPostDate = appFindPost.getDate();

            if (appFindPostDate > lastPostDate) {
                appFindPosts.add(appFindPost);

                if (appFindPostDate > newLastPostDate) {
                    newLastPostDate = appFindPostDate;
                }

            }

        }
        groupsBase.updateGroupLastPost(groupScreenName, newLastPostDate);
        List<String> vkParsedPosts = VkPostsParser.parsePosts(appFindPosts);
        return vkParsedPosts.isEmpty() ? Optional.empty() : Optional.of(vkParsedPosts);
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
