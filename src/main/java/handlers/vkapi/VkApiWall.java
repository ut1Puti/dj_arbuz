package handlers.vkapi;

import com.vk.api.sdk.actions.Wall;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
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
public class VkApiWall extends Wall {
    /** Поле для взаимодействия с группами vk */
    private final VkApiGroups groups;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param client - клиент vk
     * @param groups - класс для взаимодействия с группами
     */
    public VkApiWall(VkApiClient client, VkApiGroups groups) {
        super(client);
        this.groups = groups;
    }

    /**
     * Метод получает новые посты в группе
     *
     * @param groupName - имя группы
     * @param serviceActor - пользователь в виде нашего приложения в вк
     * @param dateOfLastPost - дата последнего поста полученного из этой группы
     * @return список новых постов в группе, max = 100
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getNewPosts(String groupName, ServiceActor serviceActor, int dateOfLastPost) throws ApiException, NoGroupException, ClientException {
        int amountOfPosts = 5;
        int latestPostDate = Integer.MAX_VALUE;
        List<WallpostFull> userFindGroupPosts = new ArrayList<>();
        while (latestPostDate > dateOfLastPost) {
            userFindGroupPosts = getPosts(serviceActor, groupName, amountOfPosts);
            amountOfPosts *= 2;
            latestPostDate = userFindGroupPosts.get(userFindGroupPosts.size() - 1).getDate();

            if (amountOfPosts > 100) {
                break;
            }

        }
        userFindGroupPosts = userFindGroupPosts.stream()
                .filter(userFindGroupPost -> userFindGroupPost.getDate() > dateOfLastPost).toList();
        List<String> groupFindPosts = createGroupPostsStrings(groupName, userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получает последние посты из сообщества
     *
     * @param amountOfPosts - кол-во постов
     * @param groupName   - Название группы
     * @param callingUser - пользователя
     * @return текст указанного кол-ва постов, а также изображения и ссылки, если они есть в посте
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws NoGroupException - возникает если не нашлась группа по заданной подстроке
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    public Optional<List<String>> getLastPosts(int amountOfPosts, String groupName, User callingUser) throws ApiException, NoGroupException, ClientException {
        Group userFindGroup = groups.searchGroup(groupName, callingUser);
        List<WallpostFull> userFindGroupPosts = getPosts(callingUser, userFindGroup, amountOfPosts);
        List<String> groupFindPosts = createGroupPostsStrings(userFindGroup.getScreenName(), userFindGroupPosts);
        return groupFindPosts.isEmpty() ? Optional.empty() : Optional.of(groupFindPosts);
    }

    /**
     * Метод получающий посты из группы в представлении вк
     *
     * @param callingUser - пользователь бота вызвавший метод
     * @param userFindGroup - группа, в которой ищем
     * @param amountOfPosts - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    private List<WallpostFull> getPosts(User callingUser, Group userFindGroup, int amountOfPosts) throws ClientException, ApiException {
        return get(callingUser)
                .domain(userFindGroup.getScreenName())
                .offset(VkApiConsts.DEFAULT_OFFSET).count(amountOfPosts)
                .execute().getItems();
    }

    /**
     * Метод получающий посты из группы в представлении вк
     *
     * @param serviceActor - пользователь приложения вызвавший метод
     * @param groupName - имя группы, в которой ищем
     * @param amountOfPosts - кол-во постов
     * @return список постов в представлении вк
     * @throws ApiException - возникает при ошибке обращения к vk api со стороны vk
     * @throws ClientException - возникает при ошибке обращения к vk api со стороны клиента
     */
    private List<WallpostFull> getPosts(ServiceActor serviceActor, String groupName, int amountOfPosts) throws ClientException, ApiException {
        return get(serviceActor)
                .domain(groupName)
                .offset(VkApiConsts.DEFAULT_OFFSET).count(amountOfPosts)
                .execute().getItems();
    }

    /**
     * Метод превращающий данные из поста в текст для отправки пользователю
     *
     * @param userFindGroupScreenName - имя группы
     * @param userFindGroupPosts - посты
     * @return список постов в виде строк
     */
    private List<String> createGroupPostsStrings(String userFindGroupScreenName, List<WallpostFull> userFindGroupPosts) {
        List<String> groupFindPosts = new ArrayList<>();
        for (WallpostFull userFindGroupPost : userFindGroupPosts) {
            List<WallpostAttachment> userFindGroupPostAttachments = userFindGroupPost.getAttachments();
            StringBuilder postTextBuilder = new StringBuilder(userFindGroupPost.getText());
            boolean isNoAttachmentsInPost = false;
            while (userFindGroupPostAttachments == null) {
                List<Wallpost> userFindGroupPostCopy = userFindGroupPost.getCopyHistory();

                if (userFindGroupPostCopy == null) {
                    isNoAttachmentsInPost = true;
                    break;
                }

                userFindGroupPostAttachments = userFindGroupPostCopy.get(VkApiConsts.FIRST_ELEMENT_INDEX)
                        .getAttachments();
                postTextBuilder.append("\n").append(userFindGroupPostCopy.get(VkApiConsts.FIRST_ELEMENT_INDEX).getText());
            }

            if (isNoAttachmentsInPost) {
                groupFindPosts.add(postTextBuilder.toString());
                continue;
            }

            String postAttachments = getAttachmentsToPost(userFindGroupScreenName, userFindGroupPostAttachments);
            groupFindPosts.add(postTextBuilder.append(" ").append(postAttachments).toString());
        }
        return groupFindPosts;
    }

    /**
     * Метод добавляющий к посту ссылки на прикрепленные элементы
     * или сообщающий об их наличии, если добавить их невозможно
     *
     * @param userFindGroupPostAttachments - доп. материалы прикрепленные к посту
     */
    private String getAttachmentsToPost(String userFindGroupScreenName, List<WallpostAttachment> userFindGroupPostAttachments) {
        StringBuilder postAttachments = new StringBuilder();
        boolean impossibleToLoadAttachment = false;
        for (WallpostAttachment userFindGroupPostAttachment : userFindGroupPostAttachments) {
            String userFindGroupPostAttachmentTypeString = userFindGroupPostAttachment.getType().toString();
            switch (userFindGroupPostAttachmentTypeString) {
                case "photo" -> {
                    postAttachments.append(userFindGroupPostAttachment
                                    .getPhoto().getSizes()
                                    .get(VkApiConsts.FIRST_ELEMENT_INDEX)
                                    .getUrl())
                            .append(" ");
                }
                case "link" -> {
                    postAttachments.append(userFindGroupPostAttachment.getLink().getUrl()).append(" ");
                }
                case "audio", "video" -> {
                    impossibleToLoadAttachment = true;
                }
            }
        }

        if (impossibleToLoadAttachment) {
            postAttachments.append("\nЕсть файлы, недоступные для отображения на сторонних ресурсах.\n")
                    .append("Если хотите посмотреть их, перейдите по ссылке: ")
                    .append(VkApiConsts.VK_ADDRESS)
                    .append(userFindGroupScreenName);
        }
        return postAttachments.toString();
    }
}
