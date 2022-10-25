package socialnetworks.socialnetwork.wall;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.objects.wall.WallpostFull;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.socialnetwork.SocialNetworkException;
import user.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия со стенами социальных сетей
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface SocialNetworkWall {
    /**
     * Метод для получения {@code amountOfPosts} постов из группы в социальной сети в виде строк
     *
     * @param groupScreenName   имя группы в социальной сети
     * @param amountOfPosts     кол-во постов которое необходимо получить
     * @param userCallingMethod пользователь вызвавший метод
     * @return список строк, каждая из которых представляет собой распаршенный пост, если возникли ошибки обращения к api
     * не связанные с самим api, тогда возвращается {@code Optional.empty()}, также {@code Optional.empty()}
     * возвращается если не было найдено новых постов
     * @throws SocialNetworkException     возникает при ошибке обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    Optional<List<String>> getLastPostsStrings(String groupScreenName, int amountOfPosts, Actor userCallingMethod)
            throws SocialNetworkException, SocialNetworkAuthException;

    /**
     * Метод для получения {@code amountOfPosts} постов из группы в социальной сети в виде объектов
     * представляющих собой класс постов в реализации api
     *
     * @param groupScreenName   имя группы в социальной сети
     * @param amountOfPosts     кол-во запрашиваемых постов
     * @param userCallingMethod пользователь вызвавший метод
     * @return посты из группы в социальной сети представленные списком объектов,
     * которые представляют реализацию постов в реализации api
     * @throws SocialNetworkException     возникает при ошибке обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @throws IllegalArgumentException   должен возникать в ситуациях когда параметры переданные в метод не соответствуют
     *                                    параметрам необходимым для выполнения запросов, даже если запрос может быть выполнен, но частично проигнорировав
     *                                    параметры(например vk api позволяет получить только 100 постов за один запрос, соответственно если {@code amountOfPosts}
     *                                    будет больше 100, то должно возникнуть это исключение)
     */
    List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, Actor userCallingMethod)
            throws SocialNetworkException, SocialNetworkAuthException;
}
