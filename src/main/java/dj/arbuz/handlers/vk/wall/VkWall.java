package dj.arbuz.handlers.vk.wall;

import com.vk.api.sdk.actions.Wall;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.handlers.vk.groups.VkGroups;
import dj.arbuz.handlers.vk.VkConstants;
import dj.arbuz.handlers.vk.groups.NoGroupException;
import dj.arbuz.user.User;

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
     * Метод получает новые посты в группе
     *
     * @param groupScreenName   - имя группы
     * @param vkAppUser         - пользователь в виде нашего приложения в vk
     * @param dateOfLastGotPost - дата последнего поста полученного из этой группы
     * @return список новых постов в группе, max = 100
     * @throws ApiException    - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getNewPosts(String groupScreenName, int dateOfLastGotPost, ServiceActor vkAppUser)
            throws ApiException, ClientException {
        final int amountOfPosts = 100;
        List<WallpostFull> appFindGroupPosts = getPosts(groupScreenName, amountOfPosts, vkAppUser)
                .stream()
                .filter(appFindGroupPost -> appFindGroupPost.getDate() > dateOfLastGotPost).toList();
        List<String> groupFindPosts = createGroupPostsStrings(appFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получает последние посты из сообщества
     *
     * @param amountOfPosts         - кол-во постов
     * @param userReceivedGroupName - имя группы
     * @param userCalledMethod      - пользователь вызвавший метод
     * @return текст указанного кол-ва постов, а также изображения и ссылки, если они есть в посте
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getLastPosts(String userReceivedGroupName, int amountOfPosts, User userCalledMethod)
            throws ApiException, NoGroupException, ClientException, IllegalArgumentException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCalledMethod);
        List<WallpostFull> userFindGroupPosts = getPosts(userFindGroup.getScreenName(), amountOfPosts, userCalledMethod);
        List<String> groupFindPosts = createGroupPostsStrings(userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получающий посты из группы в представлении вк
     *
     * @param userCalledMethod - пользователь бота вызвавший метод
     * @param groupScreenName  - короткое имя группы в которой ищем посты
     * @param amountOfPosts    - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException             - возникает при ошибке обращения к vk api со стороны vk
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
            String postAttachments = getAttachmentsToPost(groupPostAttachments);
            groupFindPosts.add(
                    postTextBuilder.append(" ").append(postAttachments).append("\n").append(postURL).toString()
            );
        }
        return groupFindPosts;
    }

    /**
     * Метод добавляющий к посту ссылки на прикрепленные элементы
     * или сообщающий об их наличии, если добавить их невозможно
     *
     * @param groupPostAttachments - доп. материалы прикрепленные к посту
     */
    private String getAttachmentsToPost(List<WallpostAttachment> groupPostAttachments) {
        StringBuilder postAttachments = new StringBuilder();
        boolean impossibleToLoadAttachment = false;
        for (WallpostAttachment groupPostAttachment : groupPostAttachments) {
            String groupPostAttachmentTypeString = groupPostAttachment.getType().toString();
            switch (groupPostAttachmentTypeString) {
                case "photo" -> postAttachments.append(groupPostAttachment
                                .getPhoto().getSizes()
                                .get(VkConstants.FIRST_ELEMENT_INDEX)
                                .getUrl())
                        .append(" ");
                case "link" -> postAttachments.append(groupPostAttachment.getLink().getUrl()).append(" ");
                case "audio", "video" -> impossibleToLoadAttachment = true;
            }
        }

        if (impossibleToLoadAttachment) {
            postAttachments.append("\nЕсть файлы, недоступные для отображения на сторонних ресурсах.\n")
                    .append("Если хотите посмотреть их, перейдите по ссылке");
        }
        return postAttachments.toString();
    }
}
