package handlers.vk.wall;

import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import handlers.vk.VkConstants;

import java.util.List;

/**
 * Класс для парсинга постов vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class VkPostsParser {
    /**
     * Метод парясщий пост из vk в строку
     *
     * @param groupPost - пост из группы в vk
     * @return строку с текстом поста, а также из постов репостов и ссылку на него
     */
    public static String parsePost(WallpostFull groupPost) {
        List<WallpostAttachment> groupPostAttachments = groupPost.getAttachments();
        StringBuilder postTextBuilder = new StringBuilder(groupPost.getText());
        while (groupPostAttachments == null) {
            List<Wallpost> groupPostCopy = groupPost.getCopyHistory();

            if (groupPostCopy == null) {
                break;
            }

            groupPostAttachments = groupPostCopy.get(VkConstants.FIRST_ELEMENT_INDEX)
                    .getAttachments();
            postTextBuilder.append("\n").append(groupPostCopy.get(VkConstants.FIRST_ELEMENT_INDEX).getText());
        }

        String postURL = VkConstants.VK_ADDRESS + VkConstants.WALL_ADDRESS +
                groupPost.getOwnerId() + "_" + groupPost.getId();
        return postTextBuilder.append("\n").append(postURL).toString();
    }
}
