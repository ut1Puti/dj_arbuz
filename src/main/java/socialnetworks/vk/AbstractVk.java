package socialnetworks.vk;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.GroupIsClosed;
import com.vk.api.sdk.objects.wall.WallpostFull;
import database.GroupBase;
import database.GroupsStorage;
import socialnetworks.socialnetwork.AbstractSocialNetwork;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.groups.NoGroupException;
import socialnetworks.socialnetwork.groups.SubscribeStatus;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.vk.groups.AbstractVkGroups;
import socialnetworks.vk.groups.VkGroups;
import socialnetworks.vk.oAuth.AbstractVkAuth;
import socialnetworks.vk.oAuth.VkAuth;
import socialnetworks.vk.wall.AbstractVkWall;
import socialnetworks.vk.wall.VkWall;
import user.BotUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Абстрактный класс обработчика запросов к vk api
 *
 * @author Кедровских Олег
 * @version 1.0
 * @see AbstractSocialNetwork
 */
public abstract class AbstractVk extends AbstractSocialNetwork<Group, WallpostFull, BotUser, ServiceActor, Actor> {
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
     * @see VkAuth#getAuthUrl()
     */
    @Override
    public final String getAuthUrl() {
        return oAuth.getAuthUrl();
    }

    /**
     * Метод для асинхронного создания пользователя
     *
     * @param userSystemId id пользователя в системе
     * @return {@code CompletableFuture<User>}, который выполняет логику создания пользователя,
     * посмотреть ее можно в метода {@link socialnetworks.vk.oAuth.VkAuth#createBotUser(String)}
     */
    public CompletableFuture<BotUser> createBotUserAsync(String userSystemId) {
        return CompletableFuture.supplyAsync(() -> oAuth.createBotUser(userSystemId));
    }

    /**
     * Метод получающий ссылку на группу в vk найденную по {@code userReceivedGroupName}
     *
     * @param userReceivedGroupName Название группы
     * @param userCallingMethod     пользователь вызвавший метод
     * @return возвращает ссылку на группу в vk
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @see VkGroups#searchGroup(String, BotUser)
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
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     * @see VkGroups#searchGroup(String, BotUser)
     */
    @Override
    public final String getGroupId(String userReceivedGroupName, BotUser userCallingMethod)
            throws NoGroupException, SocialNetworkException {
        return String.valueOf(groups.searchGroup(userReceivedGroupName, userCallingMethod).getId());
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
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @see VkGroups#searchGroup(String, BotUser)
     * @see GroupsStorage#addInfoToGroup(String, String)
     */
    @Override
    public SubscribeStatus subscribeTo(GroupBase groupBase, String userReceivedGroupName, BotUser userCallingMethod)
            throws SocialNetworkException, NoGroupException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCallingMethod);

        if (userFindGroup.getIsClosed() == GroupIsClosed.CLOSED) {
            return SubscribeStatus.GROUP_IS_CLOSED;
        }

        //TODO synchronize working with subscribers
        boolean isSubscribed = groupBase.addInfoToGroup(userFindGroup.getScreenName(), userCallingMethod.getTelegramId());
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
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    @Override
    public boolean unsubscribeFrom(GroupBase groupBase, String userReceivedGroupName, BotUser userCallingMethod)
            throws NoGroupException, SocialNetworkException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCallingMethod);

        if (userFindGroup.getIsClosed() == GroupIsClosed.CLOSED) {
            return false;
        }

        //TODO synchronize working with subscribers
        return groupBase.deleteInfoFromGroup(userFindGroup.getScreenName(), userCallingMethod.getTelegramId());
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
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     * @throws IllegalArgumentException   возникает при передаче кол-ва постов большего, чем можно получить(max 100).
     *                                    Возникает при вызове пользователем не имеющем доступа к этому методу(пример из vk sdk GroupActor)
     * @see VkGroups#searchGroup(String, BotUser)
     * @see VkWall#getPostsStrings(String, int, Actor)
     */
    @Override
    public List<String> getLastPostsAsStrings(String userReceivedGroupName, int amountOfPosts, BotUser userCallingMethod)
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
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    @Override
    public List<WallpostFull> getLastPostsAsPosts(String userReceivedGroupName, int amountOfPosts, BotUser userCalledMethod) throws NoGroupException, SocialNetworkException {
        Group userFindGroup = groups.searchGroup(userReceivedGroupName, userCalledMethod);
        return wall.getPosts(userFindGroup.getScreenName(), amountOfPosts, userCalledMethod);
    }

    public List<WallpostFull> getLastPostAsPostsUnsafe(String groupScreenName, int amountOfPosts) throws SocialNetworkException {
        return wall.getPosts(groupScreenName, amountOfPosts, vkApp);
    }
}
