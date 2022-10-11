package handlers.vk.wall;

import com.vk.api.sdk.actions.Wall;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
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
     * Метод получает новые посты в группе
     *
     * @param groupName         - имя группы
     * @param vkApp             - пользователь в виде нашего приложения в вк
     * @param dateOfLastGotPost - дата последнего поста полученного из этой группы
     * @return список новых постов в группе, max = 100
     * @throws ApiException    - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getNewPosts(String groupName, int dateOfLastGotPost, ServiceActor vkApp)
            throws ApiException, ClientException {
        final int amountOfPosts = 100;
        List<WallpostFull> appFindGroupPosts = getPosts(groupName, amountOfPosts, vkApp)
                .stream()
                .filter(appFindGroupPost -> appFindGroupPost.getDate() > dateOfLastGotPost).toList();
        List<String> groupFindPosts = createGroupPostsStrings(appFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получает последние посты из сообщества
     *
     * @param amountOfPosts - кол-во постов
     * @param groupName     - имя группы
     * @param callingUser   - пользователь вызвавщий метод
     * @return текст указанного кол-ва постов, а также изображения и ссылки, если они есть в посте
     * @throws ApiException     - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException  - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getLastPosts(String groupName, int amountOfPosts, User callingUser)
            throws ApiException, NoGroupException, ClientException, IllegalArgumentException {
        Group userFindGroup = groups.searchGroup(groupName, callingUser);
        List<WallpostFull> userFindGroupPosts = getPosts(userFindGroup.getScreenName(), amountOfPosts, callingUser);
        List<String> groupFindPosts = createGroupPostsStrings(userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получающий посты из группы в представлении вк
     *
     * @param callingUser     - пользователь бота вызвавший метод
     * @param groupScreenName - короткое имя группы в которой ищем посты
     * @param amountOfPosts   - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException    - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public final List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, User callingUser)
            throws ClientException, ApiException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        return get(callingUser)
                .domain(groupScreenName)
                .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                .execute().getItems();
    }

    /**
     * Метод получающий посты из группы в представлении вк
     *
     * @param vkApp           - пользователь приложения вызвавший метод
     * @param groupScreenName - короткое имя группы, в которой ищем
     * @param amountOfPosts   - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException    - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public final List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, ServiceActor vkApp)
            throws ClientException, ApiException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        return get(vkApp)
                .domain(groupScreenName)
                .offset(VkConstants.DEFAULT_OFFSET).count(amountOfPosts)
                .execute().getItems();
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
