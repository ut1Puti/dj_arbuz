package dj.arbuz.handlers.notifications;

import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.database.GroupBase;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.socialnetworks.vk.wall.VkPostsParser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Абстрактный класс логики потока получающего новые посты.
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PostsPullingTask implements Runnable {
    /**
     * Поле хранилища групп
     *
     * @see GroupBase
     */
    protected final GroupBase groupsBase;
    /**
     * Поле обработчика обращений к vk api
     *
     * @see AbstractVk
     */
    protected final AbstractVk vk;

    /**
     * Метод получающий новые посты из vk в виде строк
     *
     * @param groupScreenName название группы из базы данных
     * @return {@code Optional.empty()} если не нашлось новых постов или группа отсутствует в базе данных,
     * иначе возвращает {@code Optional.of(newPosts)}
     */
    protected final Optional<List<String>> getNewPostsAsStrings(String groupScreenName) {
        Optional<Long> optionalLastPostDate = groupsBase.getGroupLastPostDate(groupScreenName);

        if (optionalLastPostDate.isEmpty()) {
            return Optional.empty();
        }

        long lastPostDate = optionalLastPostDate.get();
        long newLastPostDate = lastPostDate;
        final int maxAmountOfPosts = 100;
        List<WallpostFull> appFindNewPosts = new ArrayList<>();
        List<WallpostFull> appFindPosts;
        try {
            appFindPosts = vk.getLastPostAsPostsUnsafe(groupScreenName, maxAmountOfPosts);
        } catch (SocialNetworkException e) {
            return Optional.empty();
        }
        for (WallpostFull appFindPost : appFindPosts) {
            int appFindPostDate = appFindPost.getDate();

            if (appFindPostDate > lastPostDate) {
                appFindNewPosts.add(appFindPost);

                if (appFindPostDate > newLastPostDate) {
                    newLastPostDate = appFindPostDate;
                }

            }

        }

        if (newLastPostDate != lastPostDate) {
            groupsBase.updateGroupLastPost(groupScreenName, newLastPostDate);
        }

        List<String> vkParsedPosts = VkPostsParser.parsePosts(appFindNewPosts);
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
    public abstract Iterable<String> getNewPosts();
}
