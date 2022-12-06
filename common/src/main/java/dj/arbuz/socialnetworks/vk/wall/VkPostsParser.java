package dj.arbuz.socialnetworks.vk.wall;

import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.socialnetworks.vk.VkConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для парсинга постов vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkPostsParser {
    /**
     * Поле регулярного выражения для нахождения характеристик группы и вытягивание из найденной подстроки id группы
     */
    private static final Pattern groupStatsRegex = Pattern.compile("\\[(?<id>\\w+)[|](?<name>[@а-яА-Я\\w\\s]+)]");

    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private VkPostsParser() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Метод превращающий {@link WallpostFull} в строки
     *
     * @param groupPosts - посты
     * @return список постов в виде строк
     * @see VkPostsParser#parsePost(WallpostFull)
     */
    public static List<String> parsePosts(List<WallpostFull> groupPosts) {
        List<String> groupPostsParsed = new ArrayList<>();
        for (WallpostFull groupPost : groupPosts) {
            groupPostsParsed.add(parsePost(groupPost));
        }
        return groupPostsParsed;
    }

    /**
     * Метод парясщий {@link WallpostFull} в строку
     *
     * @param groupPost - пост из группы в vk
     * @return строку с текстом поста, а также из постов репостов, и ссылку на него
     * @see WallpostFull#getAttachments()
     * @see WallpostFull#getText()
     * @see WallpostFull#getCopyHistory()
     */
    public static String parsePost(WallpostFull groupPost) {
        List<WallpostAttachment> groupPostAttachments = groupPost.getAttachments();
        StringBuilder postTextBuilder = new StringBuilder(groupPost.getText());
        while (groupPostAttachments == null) {
            List<Wallpost> groupPostCopy = groupPost.getCopyHistory();

            if (groupPostCopy == null) {
                break;
            }

            groupPostAttachments = groupPostCopy.get(VkConstants.FIRST_ELEMENT_INDEX).getAttachments();
            postTextBuilder.append("\n").append(groupPostCopy.get(VkConstants.FIRST_ELEMENT_INDEX).getText());
        }
        Matcher groupInfoMatcher = groupStatsRegex.matcher(postTextBuilder);
        while (groupInfoMatcher.find()) {
            postTextBuilder.replace(0, postTextBuilder.length(), groupInfoMatcher.replaceFirst(createGroupLinkAndScreenNameString(groupInfoMatcher.group("id"), groupInfoMatcher.group("name"))));
            groupInfoMatcher = groupStatsRegex.matcher(postTextBuilder);
        }
        String postURL = VkConstants.VK_WALL_ADDRESS + groupPost.getOwnerId() + "_" + groupPost.getId();
        return postTextBuilder.append("\n").append(postURL).toString();
    }

    /**
     * Метод формирующий строку формата: {@code link (group screen name}
     *
     * @param groupId id группы
     * @param groupScreenName короткое имя группы
     * @return строку указанного выше формата
     */
    private static String createGroupLinkAndScreenNameString(String groupId, String groupScreenName) {
        return '[' + groupScreenName + "](" + VkConstants.VK_ADDRESS + groupId + ')';
    }
}
