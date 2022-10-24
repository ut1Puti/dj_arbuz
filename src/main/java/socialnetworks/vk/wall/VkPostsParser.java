package socialnetworks.vk.wall;

import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import socialnetworks.vk.VkConstants;

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
    private static final Pattern groupStatsRegex = Pattern.compile("\\[(?<id>\\w+)[|][@а-яА-Я\\w\\s]+]");

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
        Matcher groupStatsMatcher = groupStatsRegex.matcher(postTextBuilder);
        while (groupStatsMatcher.find() && groupStatsMatcher.groupCount() == 1) {
            String groupLink = VkConstants.VK_ADDRESS + groupStatsMatcher.group("id");
            postTextBuilder.replace(0, postTextBuilder.length(), groupStatsMatcher.replaceFirst(groupLink));
            groupStatsMatcher = groupStatsRegex.matcher(postTextBuilder);
        }
        String postURL = VkConstants.VK_WALL_ADDRESS + groupPost.getOwnerId() + "_" + groupPost.getId();
        return postTextBuilder.append("\n").append(postURL).toString();
    }
}
