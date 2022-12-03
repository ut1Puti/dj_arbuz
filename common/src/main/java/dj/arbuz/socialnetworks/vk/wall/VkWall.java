package dj.arbuz.socialnetworks.vk.wall;

import dj.arbuz.BotTextResponse;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAuthException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import dj.arbuz.socialnetworks.vk.VkConstants;
import dj.arbuz.user.BotUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для взаимодействия с постами на стене в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractVkWall
 */
public final class VkWall extends AbstractVkWall {
    /**
     * Поле регулярного выражения для поиска id группы в
     */
    private static final Pattern GROUP_ID_SEARCH = Pattern.compile("club(?<id>\\d+)");
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
     *                          {@link BotUser}, {@link UserActor}, {@link ServiceActor}
     * @return текст указанного кол-ва постов, а также ссылки на другие группы, указанные в посте, и ссылки на посты
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @throws IllegalArgumentException   если пользователь от имени которого был отправлен запрос не имеет доступа к этому методу,
     *                                    а также если было запрошено больше 100 постов
     * @see VkWall#getPosts(String, int, Actor)
     * @see VkPostsParser#parsePosts(List)
     */
    @Override
    public List<String> getPostsStrings(String groupScreenName, int amountOfPosts, Actor userCallingMethod)
            throws SocialNetworkException {
        List<WallpostFull> groupFindPosts = getPosts(groupScreenName, amountOfPosts, userCallingMethod);
        return VkPostsParser.parsePosts(groupFindPosts);
    }

    /**
     * Метод получающий последние {@code amountOfPosts} постов из группы в представлении вк
     *
     * @param userCalledMethod  пользователь бота вызвавший метод доступные пользователи
     *                          {@link BotUser}, {@link UserActor}, {@link ServiceActor}
     * @param groupSearchString короткое имя группы в которой ищем посты
     * @param amountOfPosts     кол-во постов
     * @return список постов в виде {@link WallpostFull}
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @throws IllegalArgumentException   возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                    Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     * @see VkWall#searchPostsInGroup(String, int, Actor)
     */
    @Override
    public List<WallpostFull> getPosts(String groupSearchString, int amountOfPosts, Actor userCalledMethod) throws SocialNetworkException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        List<WallpostFull> groupFindPosts = searchPostsInGroup(groupSearchString, amountOfPosts, userCalledMethod);

        if (groupFindPosts == null) {
            throw new IllegalArgumentException("Этот пользователь не имеет доступа к этому методу");
        }

        if (!groupFindPosts.isEmpty()) {

            if (isUserPosts(groupFindPosts, userCalledMethod)) {
                return new ArrayList<>();
            }

        }

        return groupFindPosts;
    }

    /**
     * @param userCalledMethod  пользователь бота вызвавший метод доступные пользователи
     *                          {@link BotUser}, {@link UserActor}, {@link ServiceActor}
     * @param groupSearchString короткое имя группы в которой ищем посты
     * @param amountOfPosts     кол-во постов
     * @return список постов в виде {@link WallpostFull}, возвращает {@code null} если полученный пользователь не имеет доступа к этому методу
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @see VkWall#searchPostsByGroupId(int, int, Actor)
     * @see VkWall#searchPostsByGroupScreenName(String, int, Actor)
     */
    private List<WallpostFull> searchPostsInGroup(String groupSearchString, int amountOfPosts, Actor userCalledMethod)
            throws SocialNetworkException {
        final int groupSign = -1;
        Matcher matcher = GROUP_ID_SEARCH.matcher(groupSearchString);
        try {

            if (matcher.find()) {
                int groupPostsSearchingId = Integer.parseInt(matcher.group("id")) * groupSign;
                return searchPostsByGroupId(groupPostsSearchingId, amountOfPosts, userCalledMethod);
            } else {
                return searchPostsByGroupScreenName(groupSearchString, amountOfPosts, userCalledMethod);
            }

        } catch (ApiAuthException e) {
            throw new SocialNetworkAuthException(BotTextResponse.UPDATE_TOKEN, e);
        } catch (ApiException | ClientException e) {
            throw new SocialNetworkException(BotTextResponse.VK_API_ERROR, e);
        }
    }

    /**
     * Метод для поиска постов по id группы
     *
     * @param groupPostSearchingId id группы, в которой ищется пост
     * @param amountOfPosts        запрашиваемое кол-во постов
     * @param userCalledMethod     пользователь вызвавший метод
     *                             {@link BotUser}, {@link UserActor}, {@link ServiceActor}
     * @return список постов в представлении vk, возвращает {@code null} если полученный пользователь не имеет доступа к этому методу
     * @throws ClientException возникает при ошибке обращения к vk api со стороны пользователя
     * @throws ApiException    возникает при ошибке обработки обращения на стороне vk
     * @see BotUser
     * @see ServiceActor
     * @see com.vk.api.sdk.actions.Wall#get(ServiceActor)
     * @see com.vk.api.sdk.actions.Wall#get(UserActor)
     * @see VkWall#isUserPosts(List, Actor)
     */
    private List<WallpostFull> searchPostsByGroupId(int groupPostSearchingId, int amountOfPosts, Actor userCalledMethod)
            throws ClientException, ApiException {
        final int minGroupId = -1;

        if (groupPostSearchingId > minGroupId) {
            throw new IllegalArgumentException("Id группы не может быть не отрицательным числом");
        }

        if (userCalledMethod instanceof BotUser) {
            return vkApiClient.wall().get((BotUser) userCalledMethod)
                    .ownerId(groupPostSearchingId)
                    .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                    .execute().getItems();
        }

        if (userCalledMethod instanceof UserActor) {
            return vkApiClient.wall().get((UserActor) userCalledMethod)
                    .ownerId(groupPostSearchingId)
                    .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                    .execute().getItems();
        }

        if (userCalledMethod instanceof ServiceActor) {
            return vkApiClient.wall().get((ServiceActor) userCalledMethod)
                    .ownerId(groupPostSearchingId)
                    .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                    .execute().getItems();
        }

        return null;
    }

    /**
     * Метод для получения постов из группы по ее screenName
     *
     * @param groupScreenName  screenName группы, в которой будут искаться посты
     * @param amountOfPosts    запрашиваемое кол-во постов
     * @param userCalledMethod пользователь вызвавший метод
     *                         {@link BotUser}, {@link UserActor}, {@link ServiceActor}
     * @return список постов в представлении vk, возвращает {@code null} если полученный пользователь не имеет доступа к этому методу
     * @throws ClientException возникает при ошибке обращения к vk api со стороны пользователя
     * @throws ApiException    возникает при ошибке обработки обращения на стороне vk
     * @see BotUser
     * @see ServiceActor
     * @see com.vk.api.sdk.actions.Wall#get(ServiceActor)
     * @see com.vk.api.sdk.actions.Wall#get(UserActor)
     * @see VkWall#isUserPosts(List, Actor)
     */
    private List<WallpostFull> searchPostsByGroupScreenName(String groupScreenName, int amountOfPosts, Actor userCalledMethod) throws ClientException, ApiException {
        if (userCalledMethod instanceof BotUser) {
            return vkApiClient.wall().get((BotUser) userCalledMethod)
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

        return null;
    }

    /**
     * Метод определяющий являются ли найденные посты, постами со страницы пользователя
     *
     * @param groupFindPosts   найденные по подстроке посты
     * @param userCalledMethod пользователь вызвавший метод
     * @return {@code true} если id владельца постов и пользователя запросивших их не совпадают,
     * {@code false} если id владельца постов и пользователя запросивших их совпадают
     */
    private boolean isUserPosts(List<WallpostFull> groupFindPosts, Actor userCalledMethod) {
        return Objects.equals(groupFindPosts.get(VkConstants.FIRST_ELEMENT_INDEX).getOwnerId(), userCalledMethod.getId());
    }
}
