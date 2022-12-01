package dj.arbuz.socialnetworks.vk.groups;

import com.vk.api.sdk.objects.groups.Group;
import dj.arbuz.socialnetworks.socialnetwork.groups.SocialNetworkGroups;
import dj.arbuz.user.BotUser;

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
