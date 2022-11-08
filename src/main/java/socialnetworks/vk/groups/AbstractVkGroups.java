package socialnetworks.vk.groups;

import com.vk.api.sdk.objects.groups.Group;
import socialnetworks.socialnetwork.groups.SocialNetworkGroups;
import user.BotUser;

/**
 * Абстрактный класс для взаимодействия с группами в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see SocialNetworkGroups
 */
public abstract class AbstractVkGroups implements SocialNetworkGroups<Group, BotUser> {
    /**
     * Конструктор - создает экземпляр класса
     */
    protected AbstractVkGroups() {
    }
}
