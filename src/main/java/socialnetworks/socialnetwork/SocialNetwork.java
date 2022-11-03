package socialnetworks.socialnetwork;

import database.GroupsStorage;
import socialnetworks.socialnetwork.groups.NoGroupException;
import socialnetworks.socialnetwork.groups.SubscribeStatus;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import user.CreateUser;
import user.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия с социальными сетями
 *
 * @author Кедровсикх Олег
 * @version 1.0
 * @see CreateUser
 */
public interface SocialNetwork extends CreateUser {
    /**
     * Метод получающий ссылку для аутентификации пользователя с помощью социальной сети
     *
     * @return ссылку для аутентификации пользователя, {@code null} если ссылка отсутствует
     */
    String getAuthUrl();

    /**
     * Метод получающий ссылку на группу, найденную по подстроке полученной от пользователя
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param userCallingMethod пользователя отправивший запрос
     * @return ссылку на группу, найденную по подстроке полученной от пользователя
     * @throws NoGroupException возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    String getGroupUrl(String userReceivedGroupName, User userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод получающий id группы по подстроке полученной от пользователя
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param userCallingMethod пользователь вызвавший метод
     * @return id группы найденной по подстроке пользователя
     * @throws NoGroupException возникает если группа по подстроке была не найдена
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    String getGroupId(String userReceivedGroupName, User userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод подписывающий пользователя на группу, найденную в социальной сети
     *
     * @param groupsStorage хранилище групп и информации о них
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param userCallingMethod пользователь вызвавший метод
     * @return статус подписки на группу,
     * {@link SubscribeStatus#SUBSCRIBED} означает что пользователь успешно подписан,
     * {@link SubscribeStatus#ALREADY_SUBSCRIBED} сообщает, что пользователь уже подписан на эту группу,
     * {@link SubscribeStatus#GROUP_IS_CLOSED} сообщает, что невозможно подписаться, тк группа закрыта
     * @throws NoGroupException возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    SubscribeStatus subscribeTo(GroupsStorage groupsStorage, String userReceivedGroupName, User userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод отписывающий пользователя от группы, то есть удаляющий информацию о том, что он подписан из базы данных
     *
     * @param groupsStorage база данных
     * @param userReceivedGroupName название группы полученное от пользователя
     * @param userCallingMethod пользователь вызвавший метод
     * @return {@code true} если пользователь был отписан от группы,
     * {@code false} если пользователь не был отписан от группы
     * @throws NoGroupException возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    boolean unsubscribeFrom(GroupsStorage groupsStorage, String userReceivedGroupName, User userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод получающий последние {@code amountOfPosts} постов из социальной сети в виде строк
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param amountOfPosts кол-во постов, которые запросили в социальной сети
     * @param userCallingMethod пользователь вызвавший метод
     * @return {@code amountOfPosts} постов в виде строк
     * @throws NoGroupException возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    List<String> getLastPostsAsStrings(String userReceivedGroupName, int amountOfPosts, User userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод получающий новые посты из социальной сети по данным из базы данных,
     * а также обновляющая дату последних постов в базе данных
     *
     * @param groupsStorage база данных из которой получаются данные, должна содержать дату последнего поста и название группы
     * @param groupScreenName название группы, которая будет искаться в базе данных
     * @return новые посты в виде строк
     * {@code Optional.empty()} если возникли ошибки обращения к api не связанные с самим api или если посты не были найдены
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    Optional<List<String>> getNewPosts(GroupsStorage groupsStorage, String groupScreenName)
            throws SocialNetworkException;
}
