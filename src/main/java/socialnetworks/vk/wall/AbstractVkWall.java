package socialnetworks.vk.wall;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.objects.wall.WallpostFull;
import socialnetworks.socialnetwork.wall.SocialNetworkWall;

/**
 * Абстрактный класс стены vk
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see SocialNetworkWall
 */
public abstract class AbstractVkWall implements SocialNetworkWall<WallpostFull, Actor> {
    /**
     * Конструктор - создает экземпляр класса
     */
    protected AbstractVkWall() {
    }
}
