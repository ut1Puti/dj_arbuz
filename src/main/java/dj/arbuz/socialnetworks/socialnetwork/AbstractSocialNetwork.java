package dj.arbuz.socialnetworks.socialnetwork;

import dj.arbuz.socialnetworks.socialnetwork.groups.SocialNetworkGroups;
import dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import dj.arbuz.socialnetworks.socialnetwork.wall.SocialNetworkWall;

/**
 * Абстрактный класс социальной сети
 *
 * @param <G> параметр типа данных используемый в качестве группы социальной сети
 * @param <P> параметр типа данных, который используется для представления поста со стены в социальной сети
 * @param <U> параметр типа данных используемого в качестве пользователя
 * @param <S> параметр типа данных используемого в качестве пользователя приложения социальной сети
 * @param <A> параметр типа данных используемого в качестве пользователя для стены социальной сети
 * @author Кедровских Олег
 * @version 1.0
 */
public abstract class AbstractSocialNetwork<G, P, U, S, A> implements SocialNetwork<P, U> {
    /**
     * Поле класс для авторизации в социальной сети
     *
     * @see SocialNetworkAuth
     */
    protected final SocialNetworkAuth<S, U> oAuth;
    /**
     * Поле класса групп социальной сети
     *
     * @see SocialNetworkGroups
     */
    protected final SocialNetworkGroups<G, U> groups;
    /**
     * Поле класса стены социальной сети
     *
     * @see SocialNetworkWall
     */
    protected final SocialNetworkWall<P, A> wall;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param oAuth класс авторизации в социальной сети
     * @param groups класс групп социальной сети
     * @param wall класс стены социальной сети
     */
    protected AbstractSocialNetwork(SocialNetworkAuth<S, U> oAuth, SocialNetworkGroups<G, U> groups, SocialNetworkWall<P, A> wall) {
        this.oAuth = oAuth;
        this.groups = groups;
        this.wall = wall;
    }
}
