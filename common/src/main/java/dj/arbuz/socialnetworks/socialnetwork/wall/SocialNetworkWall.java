package dj.arbuz.socialnetworks.socialnetwork.wall;

import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;

import java.util.List;

/**
 * Интерфейс для взаимодействия со стенами социальных сетей
 *
 * @param <P> параметр типа данных, который используется для представления поста со стены в социальной сети
 * @param <A> параметр типа данных используемого в качестве пользователя
 * @author Кедровских Олег
 * @version 1.0
 */
public interface SocialNetworkWall<P, A> {
    /**
     * Метод для получения {@code amountOfPosts} постов из группы в социальной сети в виде строк
     *
     * @param groupScreenName   имя группы в социальной сети
     * @param amountOfPosts     кол-во постов которое необходимо получить
     * @param userCallingMethod пользователь вызвавший метод
     * @return список строк, каждая из которых представляет собой распаршенный пост, метод не может возвращать {@code null},
     * при необходимости вернуть {@code null} создавайте пустой лист
     * @throws SocialNetworkException     возникает при ошибке обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    List<String> getPostsStrings(String groupScreenName, int amountOfPosts, A userCallingMethod)
            throws SocialNetworkException, SocialNetworkAuthException;

    /**
     * Метод для получения {@code amountOfPosts} постов из группы в социальной сети в виде объектов
     * представляющих собой класс постов в реализации api
     *
     * @param groupScreenName   имя группы в социальной сети
     * @param amountOfPosts     кол-во запрашиваемых постов
     * @param userCallingMethod пользователь вызвавший метод
     * @return посты из группы в социальной сети представленные списком объектов,
     * которые представляют реализацию постов в реализации api, метод не может возвращать {@code null},
     * при необходимости вернуть {@code null} создавайте пустой лист
     * @throws SocialNetworkException     возникает при ошибке обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @throws IllegalArgumentException   должен возникать в ситуациях когда параметры переданные в метод не соответствуют
     *                                    параметрам необходимым для выполнения запросов, даже если запрос может быть выполнен, но частично проигнорировав
     *                                    параметры(например vk api позволяет получить только 100 постов за один запрос, соответственно если {@code amountOfPosts}
     *                                    будет больше 100, то должно возникнуть это исключение)
     */
    List<P> getPosts(String groupScreenName, int amountOfPosts, A userCallingMethod)
            throws SocialNetworkException, SocialNetworkAuthException, SocialNetworkException;
}
