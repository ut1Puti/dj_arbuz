package dj.arbuz.socialnetworks.vk;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.GroupIsClosed;
import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.database.GroupBase;
import dj.arbuz.socialnetworks.socialnetwork.AbstractSocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.socialnetwork.groups.SubscribeStatus;
import dj.arbuz.socialnetworks.vk.groups.AbstractVkGroups;
import dj.arbuz.socialnetworks.vk.oAuth.AbstractVkAuth;
import dj.arbuz.socialnetworks.vk.wall.AbstractVkWall;
import dj.arbuz.socialnetworks.vk.oAuth.VkAuth;
import dj.arbuz.user.BotUser;

import java.util.List;

/**
 * Абстрактный класс обработчика запросов к vk api
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractSocialNetwork
 */
public class AbstractVk extends AbstractSocialNetwork<Group, WallpostFull, BotUser, ServiceActor, Actor, GroupActor> {
    /**
     * Поле пользователя приложения vk
     */
    protected final ServiceActor vkApp;

    /**
     * Конструктор - создает экземпляр классов
     *
     * @param auth   класс для авторизации пользователя в vk
     * @param groups класс реализующий взаимодействие с группами в vk
     * @param wall   класс реализующий взаимодействие со стеной vk
     */
    protected AbstractVk(AbstractVkAuth auth, AbstractVkGroups groups, AbstractVkWall wall) {
        super(auth, groups, wall);
        vkApp = auth.createAppActor();
    }

    /**
     * Метод обертка возвращающий ссылку для аутентификации
     *
     * @return ссылку для аутентификации, если сервер недоступен, то эта ссылка {@code null}
     * @see dj.arbuz.socialnetworks.vk.oAuth.VkAuth#getAuthUrl(String) 
     */
    @Override
    public final String getAuthUrl(String userTelegramId) {
        return oAuth.getAuthUrl(userTelegramId);
    }

    /**
     * Метод для асинхронного создания пользователя
     *
     * @param userSystemId id пользователя в системе
     * @return {@code CompletableFuture<User>}, который выполняет логику создания пользователя,
<<<<<<<< HEAD:common/src/main/java/dj/arbuz/socialnetworks/vk/AbstractVk.java
     * посмотреть ее можно в метода {@link dj.arbuz.socialnetworks.vk.oAuth.VkAuth#createBotUser(String)}
========
     * посмотреть ее можно в метода {@link VkAuth#createBotUser(String)}
>>>>>>>> developTaskFour:src/main/java/dj/arbuz/socialnetworks/vk/AbstractVk.java
     */
    @Override
    public BotUser createBotUser(String userSystemId) {
        return oAuth.createBotUser(userSystemId);
    }

    /**
     * Метод получающий ссылку на группу в vk найденную по {@code userReceivedGroupName}
     *
     * @param userReceivedGroupName Название группы
     * @param userCallingMethod     пользователь вызвавший метод
     * @return возвращает ссылку на группу в vk
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @see dj.arbuz.socialnetworks.vk.groups.VkGroups#searchGroup(String, BotUser) (String, BotUser)
     */
    @Override
    public final String getGroupUrl(String userReceivedGroupName, BotUser userCallingMethod)
            throws NoGroupException, SocialNetworkException {
        return VkConstants.VK_ADDRESS + groups.searchGroup(userReceivedGroupName, userCallingMethod).getScreenName();
    }

    /**
     * Метод получающий id группы найденную по {@code userReceivedGroupName}
     *
     * @param userReceivedGroupName Название группы
     * @param userCallingMethod     пользователь вызвавший метод
     * @return возвращает id группы
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @see dj.arbuz.socialnetworks.vk.groups.VkGroups#searchGroup(String, BotUser) (String, BotUser)
     */
    @Override
    public final String getGroupId(String userReceivedGroupName, BotUser userCallingMethod)
            throws NoGroupException, SocialNetworkException {
        return String.valueOf(groups.searchGroup(userReceivedGroupName, userCallingMethod).getId());
    }

    @Override
    public final List<? extends Group> searchUserAdminGroups(BotUser userCallingMethod) throws SocialNetworkException {
        return groups.searchUserAdminGroups(userCallingMethod);
    }

    /**
     * Метод для подписки пользователя(сохранение в базу данных id пользователя в телеграмме и группы)
     *
     * @param userReceivedGroupName строка по которой будет искаться группа, полученная от пользователя
     * @param userCallingMethod     пользователь вызвавший метод
     * @return статус подписки на группу,
     * {@link SubscribeStatus#SUBSCRIBED} - означает что пользователь успешно подписан,
     * {@link SubscribeStatus#ALREADY_SUBSCRIBED} - сообщает, что пользователь уже подписан на эту группу,
     * {@link SubscribeStatus#GROUP_IS_CLOSED} - сообщает, что невозможно подписаться, тк группа закрыта
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @see dj.arbuz.socialnetworks.vk.groups.VkGroups#searchGroup(String, BotUser) (String, BotUser)
     * @see GroupBase#addSubscriber(String, String) (String, String)
     */
    @Override
    public final SubscribeStatus subscribeTo(GroupBase groupBase, String userReceivedGroupName, BotUser userCallingMethod)
            throws SocialNetworkException, NoGroupException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCallingMethod);

        if (userFindGroup.getIsClosed() == GroupIsClosed.CLOSED) {
            return SubscribeStatus.GROUP_IS_CLOSED;
        }

        //TODO synchronize working with subscribers
        boolean isSubscribed = groupBase.addSubscriber(userFindGroup.getScreenName(), userCallingMethod.getTelegramId());
        return isSubscribed ? SubscribeStatus.SUBSCRIBED : SubscribeStatus.ALREADY_SUBSCRIBED;
    }

    /**
     * Метод отписывающий пользователя от группы
     *
     * @param groupBase             база данных
     * @param userReceivedGroupName название группы полученное от пользователя
     * @param userCallingMethod     пользователь вызвавший метод
     * @return {@code true} если пользователь был отписан, {@code false} если пользователь не был отписан
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    @Override
    public final boolean unsubscribeFrom(GroupBase groupBase, String userReceivedGroupName, BotUser userCallingMethod)
            throws NoGroupException, SocialNetworkException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCallingMethod);

        if (userFindGroup.getIsClosed() == GroupIsClosed.CLOSED) {
            return false;
        }

        //TODO synchronize working with subscribers
        return groupBase.deleteSubscriber(userFindGroup.getScreenName(), userCallingMethod.getTelegramId());
    }

    /**
     * Метод для получения последних {@code amountOfPosts} постов со стены по {@code userReceivedGroupName}
     *
     * @param amountOfPosts         - кол-во постов
     * @param userReceivedGroupName - имя группы
     * @param userCallingMethod     - пользователь вызвавший метод
     * @return возвращает последние amountOfPosts постов
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @throws IllegalArgumentException   возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                    Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     * @see dj.arbuz.socialnetworks.vk.groups.VkGroups#searchGroup(String, BotUser) (String, BotUser)
     * @see dj.arbuz.socialnetworks.vk.wall.VkWall#getPostsStrings(String, int, Actor) (String, int, Actor)
     */
    @Override
    public final List<String> getLastPostsAsStrings(String userReceivedGroupName, int amountOfPosts, BotUser userCallingMethod)
            throws NoGroupException, SocialNetworkException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCallingMethod);
        return wall.getPostsStrings(userFindGroup.getScreenName(), amountOfPosts, userCallingMethod);
    }

    /**
     * Метод для получения постов из группы vk, посты представлены классом постов vk
     *
     * @param userReceivedGroupName подстрока полученная от пользователя
     * @param amountOfPosts         кол-во постов, которые запросили в социальной сети
     * @param userCalledMethod      пользователь вызвавший метод
     * @return {@code amountOfPosts} постов в виде {@code WallpostFull}
     * @throws NoGroupException           возникает если группа по заданной подстроке не была найдена
     * @throws SocialNetworkException     возникает при ошибках обращения к api социальной сети
     * @throws dj.arbuz.socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    @Override
    public final List<WallpostFull> getLastPostsAsPosts(String userReceivedGroupName, int amountOfPosts, BotUser userCalledMethod) throws NoGroupException, SocialNetworkException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCalledMethod);
        return wall.getPosts(userFindGroup.getScreenName(), amountOfPosts, userCalledMethod);
    }

    public final List<WallpostFull> getLastPostAsPostsUnsafe(String groupScreenName, int amountOfPosts) throws SocialNetworkException {
        return wall.getPosts(groupScreenName, amountOfPosts, vkApp);
    }
}
