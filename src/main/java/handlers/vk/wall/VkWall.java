package handlers.vk.wall;

import com.vk.api.sdk.actions.Wall;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiAuthException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import database.GroupsStorage;
import handlers.vk.groups.VkGroups;
import handlers.vk.VkConstants;
import handlers.vk.groups.NoGroupException;
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
public class VkWall extends Wall {
    /**
     * Поле для взаимодействия с группами vk
     */
    private final VkGroups groups;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param client - клиент vk
     * @param groups - класс для взаимодействия с группами
     */
    public VkWall(VkApiClient client, VkGroups groups) {
        super(client);
        this.groups = groups;
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
     */
    public Optional<List<String>> getNewPosts(GroupsStorage groupBase, String groupScreenName, ServiceActor vkAppUser)
            throws ClientException, ApiException {
        final int amountOfPosts = 100;
        Optional<Integer> optionalLastPostDate = groupBase.getGroupLastPostDate(groupScreenName);

        if (optionalLastPostDate.isEmpty()) {
            return Optional.empty();
        }

        int lastPostDate = optionalLastPostDate.get();
        List<WallpostFull> appFindPosts = new ArrayList<>();
        int newLastPostDate = lastPostDate;
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
     * @param userReceivedGroupName - имя группы
     * @param userCallingMethod     - пользователь вызвавший метод
     * @return текст указанного кол-ва постов, а также изображения и ссылки, если они есть в посте
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
     * @throws ApiAuthException         - возникает при необходимости продлить токен путем повторной авторизации
     * @throws NoGroupException         - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException          - возникает при ошибке обращения к vk api со стороны клиента
     * @throws IllegalArgumentException - возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                  Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     */
    public Optional<List<String>> getLastPosts(String userReceivedGroupName, int amountOfPosts, User userCallingMethod)
            throws ApiException, NoGroupException, ClientException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCallingMethod);
        List<WallpostFull> userFindGroupPosts = getPosts(userFindGroup.getScreenName(), amountOfPosts, userCallingMethod);
        List<String> groupFindPosts = createGroupPostsStrings(userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получающий последние n постов из группы в представлении вк
     *
     * @param userCalledMethod - пользователь бота вызвавший метод
     * @param groupScreenName  - короткое имя группы в которой ищем посты
     * @param amountOfPosts    - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
     * @throws ApiAuthException         - возникает при необходимости продлить токен путем повторной авторизации
     * @throws ClientException          - возникает при ошибке обращения к vk api со стороны клиента
     * @throws IllegalArgumentException - возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                  Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     */
    public List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, Actor userCalledMethod)
            throws ClientException, ApiException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        if (userCalledMethod instanceof User) {
            return get((UserActor) userCalledMethod)
                    .domain(groupScreenName)
                    .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                    .execute().getItems();
        }

        if (userCalledMethod instanceof ServiceActor) {
            return get((ServiceActor) userCalledMethod)
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
     */
    private List<String> createGroupPostsStrings(List<WallpostFull> groupPosts) {
        List<String> groupFindPosts = new ArrayList<>();
        for (WallpostFull groupPost : groupPosts) {
            List<WallpostAttachment> groupPostAttachments = groupPost.getAttachments();
            StringBuilder postTextBuilder = new StringBuilder(groupPost.getText());
            boolean isNoAttachmentsInPost = false;
            while (groupPostAttachments == null) {
                List<Wallpost> groupPostCopy = groupPost.getCopyHistory();

                if (groupPostCopy == null) {
                    isNoAttachmentsInPost = true;
                    break;
                }

                groupPostAttachments = groupPostCopy.get(VkConstants.FIRST_ELEMENT_INDEX)
                        .getAttachments();
                postTextBuilder.append("\n").append(groupPostCopy.get(VkConstants.FIRST_ELEMENT_INDEX).getText());
            }

            if (isNoAttachmentsInPost) {
                groupFindPosts.add(postTextBuilder.toString());
                continue;
            }

            String postURL = VkConstants.VK_ADDRESS + VkConstants.WALL_ADDRESS +
                    groupPost.getOwnerId() + "_" + groupPost.getId();
            groupFindPosts.add(
                    postTextBuilder.append("\n").append(postURL).toString()
            );
        }
        return groupFindPosts;
    }
}
