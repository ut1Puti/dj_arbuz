package socialnetworks.vk.wall;

import bots.BotTextResponse;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAuthException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.WallpostFull;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.wall.SocialNetworkWall;
import socialnetworks.vk.VkConstants;
import user.User;

import java.util.List;
import java.util.Optional;

/**
 * Класс для взаимодействия с постами на стене в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see SocialNetworkWall
 */
public class VkWall implements SocialNetworkWall {
    /**
     * Поле класс позволяющего работать с vk api
     */
    private final VkApiClient vkApiClient;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param vkApiClient - клиент vk
     */
    public VkWall(VkApiClient vkApiClient) {
        this.vkApiClient = vkApiClient;
    }

    /**
     * Метод получает последние {@code amountOfPosts} посты из сообщества
     *
     * @param amountOfPosts     кол-во постов
     * @param groupScreenName   имя группы
     * @param userCallingMethod пользователь вызвавший метод, доступные пользователи
     *                          {@link User}, {@link UserActor}, {@link ServiceActor}
     * @return текст указанного кол-ва постов, а также ссылки на другие группы, указанные в посте, и ссылки на посты,
     * {@code Optional.empty()} возникает при ошибках обращения к vk api
     * не связанных с самим api, а также в группе с указанным именем не было найдено постов
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @see VkWall#getPosts(String, int, Actor)
     * @see VkPostsParser#parsePosts(List)
     */
    @Override
    public Optional<List<String>> getLastPostsStrings(String groupScreenName, int amountOfPosts, Actor userCallingMethod)
            throws SocialNetworkException, SocialNetworkAuthException {
        List<WallpostFull> userFindGroupPosts;
        try {
            userFindGroupPosts = getPosts(groupScreenName, amountOfPosts, userCallingMethod);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        List<String> groupFindPosts = VkPostsParser.parsePosts(userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получающий последние {@code amountOfPosts} постов из группы в представлении вк
     *
     * @param userCalledMethod пользователь бота вызвавший метод доступные пользователи
     *                         {@link User}, {@link UserActor}, {@link ServiceActor}
     * @param groupScreenName  короткое имя группы в которой ищем посты
     * @param amountOfPosts    кол-во постов
     * @return список постов в виде {@link WallpostFull}
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @throws IllegalArgumentException   возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                    Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     * @see User
     * @see ServiceActor
     * @see com.vk.api.sdk.actions.Wall#get(ServiceActor)
     * @see com.vk.api.sdk.actions.Wall#get(UserActor)
     */
    @Override
    public List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, Actor userCalledMethod)
            throws SocialNetworkException, SocialNetworkAuthException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        try {

            if (userCalledMethod instanceof User) {
                return vkApiClient.wall().get((User) userCalledMethod)
                        .domain(groupScreenName)
                        .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                        .execute().getItems();
            }

            if (userCalledMethod instanceof UserActor) {
                return vkApiClient.wall().get((UserActor) userCalledMethod)
                        .domain(groupScreenName)
                        .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                        .execute().getItems();
            }

            if (userCalledMethod instanceof ServiceActor) {
                return vkApiClient.wall().get((ServiceActor) userCalledMethod)
                        .domain(groupScreenName)
                        .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                        .execute().getItems();
            }

        } catch (ApiAuthException e) {
            throw new SocialNetworkAuthException(BotTextResponse.UPDATE_TOKEN, e);
        } catch (ApiException | ClientException e) {
            throw new SocialNetworkException(BotTextResponse.VK_API_ERROR, e);
        }

        throw new IllegalArgumentException("Этот пользователь не имеет доступа к этому методу");
    }
}
