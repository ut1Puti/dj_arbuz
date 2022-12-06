package dj.arbuz.socialnetworks.socialnetwork;

import dj.arbuz.database.GroupBase;
import dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.socialnetwork.groups.SubscribeStatus;
import dj.arbuz.user.BotUser;

import java.util.List;

/**
 * Интерфейс для взаимодействия с социальными сетями
 *
 * @param <U> параметр типа данных используемого в качестве пользователя
 * @author Кедровсикх Олег
 * @version 1.0
 */
public interface SocialNetwork<W, G, U, GU> {
    /**
     * Метод получающий ссылку для аутентификации пользователя с помощью социальной сети
     *
     * @return ссылку для аутентификации пользователя, {@code null} если ссылка отсутствует
     */
    String getAuthUrl(String userSystemId);

    /**
     * Метод для асинхронного создания пользователя
     *
     * @param userId id пользователя в системе
     * @return {@code CompletableFuture<User>}, который выполняет логику создания пользователя
     */
    U createBotUser(String userId);

    /**
     * Метод получающий ссылку на группу, найденную по подстроке полученной от пользователя
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param userCallingMethod     пользователя отправивший запрос
     * @return ссылку на группу, найденную по подстроке полученной от пользователя
     * @throws NoGroupException           возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    String getGroupUrl(String userReceivedGroupName, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод получающий id группы по подстроке полученной от пользователя
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param userCallingMethod     пользователь вызвавший метод
     * @return id группы найденной по подстроке пользователя
     * @throws NoGroupException           возникает если группа по подстроке была не найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    String getGroupId(String userReceivedGroupName, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     *
     *
     * @param userCallingMethod
     * @return
     */
    List<? extends G> searchUserAdminGroups(BotUser userCallingMethod) throws SocialNetworkException;

    /**
     * Метод подписывающий пользователя на группу, найденную в социальной сети
     *
     * @param groupsStorage         хранилище групп и информации о них
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param userCallingMethod     пользователь вызвавший метод
     * @return статус подписки на группу,
     * {@link SubscribeStatus#SUBSCRIBED} означает что пользователь успешно подписан,
     * {@link SubscribeStatus#ALREADY_SUBSCRIBED} сообщает, что пользователь уже подписан на эту группу,
     * {@link SubscribeStatus#GROUP_IS_CLOSED} сообщает, что невозможно подписаться, тк группа закрыта
     * @throws NoGroupException           возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    SubscribeStatus subscribeTo(GroupBase groupsStorage, String userReceivedGroupName, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод отписывающий пользователя от группы, то есть удаляющий информацию о том, что он подписан из базы данных
     *
     * @param groupsStorage         база данных
     * @param userReceivedGroupName название группы полученное от пользователя
     * @param userCallingMethod     пользователь вызвавший метод
     * @return {@code true} если пользователь был отписан от группы,
     * {@code false} если пользователь не был отписан от группы
     * @throws NoGroupException           возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    boolean unsubscribeFrom(GroupBase groupsStorage, String userReceivedGroupName, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод получающий последние {@code amountOfPosts} постов из социальной сети в виде строк
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param amountOfPosts         кол-во постов, которые запросили в социальной сети
     * @param userCallingMethod     пользователь вызвавший метод
     * @return {@code amountOfPosts} постов в виде строк
     * @throws NoGroupException           возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    List<String> getLastPostsAsStrings(String userReceivedGroupName, int amountOfPosts, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод получающий {@code amountOfPosts} из социальной сети в виде класса постов социальной сети
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param amountOfPosts         кол-во постов, которые запросили в социальной сети
     * @param userCalledMethod      пользователь вызвавший метод
     * @return {@code amountOfPosts} постов в виде класса постов социальной сети
     * @throws NoGroupException           возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    List<W> getLastPostsAsPosts(String userReceivedGroupName, int amountOfPosts, U userCalledMethod)
            throws NoGroupException, SocialNetworkException;
}
