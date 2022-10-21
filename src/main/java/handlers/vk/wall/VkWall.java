package handlers.vk.wall;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAuthException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.WallpostFull;
import database.GroupsStorage;
import handlers.vk.VkConstants;
import user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс для взаимодействия с постами на стене в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkWall {
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
     * Метод получающий новые посты из групп в базе данных
     *
     * @param groupBase       - база данных
     * @param groupScreenName - название группы в базе данных
     * @param vkAppUser       - пользователь вызвавший метод
     * @return список постов в группе в виде строк
     * @throws ApiException    - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     * @see GroupsStorage#getGroupLastPostDate(String)
     * @see GroupsStorage#updateGroupLastPost(String, long)
     * @see VkWall#getPosts(String, int, Actor)
     * @see VkWall#createGroupPostsStrings(List)
     */
    public Optional<List<String>> getNewPosts(GroupsStorage groupBase, String groupScreenName, ServiceActor vkAppUser)
            throws ClientException, ApiException {
        final int amountOfPosts = 100;
        //TODO synchronize working with lastPostDate
        Optional<Long> optionalLastPostDate = groupBase.getGroupLastPostDate(groupScreenName);

        if (optionalLastPostDate.isEmpty()) {
            return Optional.empty();
        }

        long lastPostDate = optionalLastPostDate.get();
        List<WallpostFull> appFindPosts = new ArrayList<>();
        long newLastPostDate = lastPostDate;
        for (WallpostFull appFindPost : getPosts(groupScreenName, amountOfPosts, vkAppUser)) {
            int appFindPostDate = appFindPost.getDate();

            if (appFindPostDate > lastPostDate) {
                appFindPosts.add(appFindPost);

                if (appFindPostDate > newLastPostDate) {
                    newLastPostDate = appFindPostDate;
                }

            }

        }
        groupBase.updateGroupLastPost(groupScreenName, newLastPostDate);
        List<String> groupsFromDataBaseFindPosts = createGroupPostsStrings(appFindPosts);
        return groupsFromDataBaseFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupsFromDataBaseFindPosts);
    }

    /**
     * Метод получает последние n посты из сообщества
     *
     * @param amountOfPosts         - кол-во постов
     * @param groupScreenName - имя группы
     * @param userCallingMethod     - пользователь вызвавший метод
     * @return текст указанного кол-ва постов, а также изображения и ссылки, если они есть в посте
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
     * @throws ApiAuthException         - возникает при необходимости продлить токен путем повторной авторизации
     * @throws ClientException          - возникает при ошибке обращения к vk api со стороны клиента
     * @throws IllegalArgumentException - возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                  Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     * @see VkWall#getPosts(String, int, Actor)
     * @see VkWall#createGroupPostsStrings(List)
     */
    public Optional<List<String>> getLastPosts(String groupScreenName, int amountOfPosts, User userCallingMethod)
            throws ApiException, ClientException {
        List<WallpostFull> userFindGroupPosts = getPosts(groupScreenName, amountOfPosts, userCallingMethod);
        List<String> groupFindPosts = createGroupPostsStrings(userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получающий последние n постов из группы в представлении вк
     *
     * @param userCalledMethod - пользователь бота вызвавший метод(User, ServiceActor)
     * @param groupScreenName  - короткое имя группы в которой ищем посты
     * @param amountOfPosts    - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
     * @throws ApiAuthException         - возникает при необходимости продлить токен путем повторной авторизации
     * @throws ClientException          - возникает при ошибке обращения к vk api со стороны клиента
     * @throws IllegalArgumentException - возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                  Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     * @see User
     * @see ServiceActor
     * @see com.vk.api.sdk.actions.Wall#get(ServiceActor)
     * @see com.vk.api.sdk.actions.Wall#get(UserActor)
     */
    public List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, Actor userCalledMethod)
            throws ClientException, ApiException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        if (userCalledMethod instanceof User) {
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

        throw new IllegalArgumentException("Этот пользователь не имеет доступа к этому методу");
    }

    /**
     * Метод превращающий данные из поста в текст для отправки пользователю
     *
     * @param groupPosts - посты
     * @return список постов в виде строк
     * @see VkPostsParser#parsePost(WallpostFull) 
     */
    private List<String> createGroupPostsStrings(List<WallpostFull> groupPosts) {
        List<String> groupFindPosts = new ArrayList<>();
        for (WallpostFull groupPost : groupPosts) {
            groupFindPosts.add(VkPostsParser.parsePost(groupPost));
        }
        return groupFindPosts;
    }
}
