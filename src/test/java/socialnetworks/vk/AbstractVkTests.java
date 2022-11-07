package socialnetworks.vk;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.wall.WallpostFull;
import database.GroupsStorage;
import org.junit.jupiter.api.Test;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.groups.NoGroupException;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.vk.groups.AbstractVkGroups;
import socialnetworks.vk.oAuth.AbstractVkAuth;
import socialnetworks.vk.wall.AbstractVkWall;
import socialnetworks.vk.wall.VkPostsParser;
import socialnetworks.socialnetwork.groups.SubscribeStatus;
import user.BotUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Класс тестирующий абстрактный класс vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class AbstractVkTests {
    /**
     * Поле id пользователя от имени которого проводятся тесты
     */
    private final String userSystemId = "userId";
    /**
     *
     */
    private final GroupsStorage groupsStorage = GroupsStorage.getInstance();
    /**
     * Поле тестовой реализации vk
     */
    private final AbstractVk vk = new TestVk();

    /**
     * Метод тестирующий получение ссылки для аутентификации
     */
    @Test
    public void testGetAuthUrl() {
        assertEquals("AUTH_URL", vk.getAuthUrl());
    }

    /**
     * Метод для тестирования создания пользователя приложения
     */
    @Test
    public void testCreateAppActor() {
        assertEquals(ServiceActor.class, vk.vkApp.getClass());
        assertEquals(new ServiceActor(-1, "CLIENT_SECRET", "ACCESS_TOKEN"), vk.vkApp);
    }

    /**
     * Метод тестирующий асинхронное создание пользователя системы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testCreateUserAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<BotUser> updateUser = vk.createBotUserAsync(userSystemId);
        assertEquals(new BotUser(1, "ACCESS_TOKEN", userSystemId), updateUser.get());
    }

    /**
     * Метод проверяющий получение ссылки на существующую группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetExistGroupUrl() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertEquals(VkConstants.VK_ADDRESS + "?screenName", vk.getGroupUrl(userReceivedGroupName, user));
    }

    /**
     * Метод проверяющий получение ссылки на группу по подстроке, которой не соответствует ни одна группа среди доступных
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testGetNotExistGroupUrl() throws ExecutionException, InterruptedException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getGroupUrl(userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод проверяющий получение id группы, на существующую группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetExistGroupId() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertEquals("?id", vk.getGroupId(userReceivedGroupName, user));
    }

    /**
     * Метод проверяющий получение id группы, которой не существует
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testGetNotExistGroupId() throws ExecutionException, InterruptedException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getGroupId(userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий подписку на существующую группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testSubscribeToNotSubscribedExistGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertEquals(SubscribeStatus.SUBSCRIBED, vk.subscribeTo(groupsStorage, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий подписку на несуществующую группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testSubscribeToNotExistGroup() throws ExecutionException, InterruptedException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.subscribeTo(groupsStorage, userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий подписку на закрытую группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testSubscribeToClosedGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertEquals(SubscribeStatus.GROUP_IS_CLOSED, vk.subscribeTo(groupsStorage, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий подписку на подписанную группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testSubscribeToSubscribedGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertEquals(SubscribeStatus.ALREADY_SUBSCRIBED, vk.subscribeTo(groupsStorage, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий отписку от существующей группу
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testUnsubscribeFromExistSubscribedGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertTrue(vk.unsubscribeFrom(groupsStorage, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий отписки от несуществующей группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testUnsubscribeFromNotExistGroup() throws ExecutionException, InterruptedException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.subscribeTo(groupsStorage, userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий отписку от закрытой группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testUnsubscribeFromClosedGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertFalse(vk.unsubscribeFrom(groupsStorage, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий отписки от существующей не подписанной группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testUnsubscribeFromExistNotSubscribedGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        assertFalse(vk.unsubscribeFrom(groupsStorage, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий получение постов в виде строк из существующей группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsStringsFromExistGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        List<String> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsStrings(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение постов в виде строк из не существующей группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testGetLastPostsAsStringsFromNotExistGroup() throws ExecutionException, InterruptedException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getLastPostsAsStrings(userReceivedGroupName, 10, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий получение постов в виде строк, в группе в которой нет постов
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsStringsFromExistGroupWithNoPosts() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        List<String> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsStrings(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение постов в виде {@code WallpostFull} из существующей группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsPostsFromExistGroup() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        List<WallpostFull> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsPosts(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение постов в виде {@code WallpostFull} из не существующей группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     */
    @Test
    public void testGetLastPostsAsPostsFromNotExistGroup() throws ExecutionException, InterruptedException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getLastPostsAsPosts(userReceivedGroupName, 10, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий получение постов в виде {@code WallpostFull} из группы в которой нет постов
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsPostsFromExistGroupWithNoPosts() throws ExecutionException, InterruptedException, NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        List<WallpostFull> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsPosts(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение новых постов в виде строк из группы
     *
     * @throws ExecutionException возникает при ошибках выполнения асинхронного создания пользователя
     * @throws InterruptedException возникает при прерывании потока выполнения создания
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetNewPostsAsStrings() throws ExecutionException, InterruptedException, SocialNetworkException {
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "";
        List<String> expectedNewPostsAsStrings = new ArrayList<>();
        assertEquals(100, vk.getNewPostsAsStrings(groupsStorage, userReceivedGroupName).get().size());
    }
}

/**
 * Класс тестовой реализации vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class TestVk extends AbstractVk {
    /**
     * Конструктор - создает экземпляр класса
     */
    TestVk() {
        super(new TestVkAuth(), new TestVkGroups(), new TestVkWall());
    }
}

/**
 * Тестовый класс авторизации в vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class TestVkAuth extends AbstractVkAuth {
    /**
     * Поле ссылки для авторизации
     */
    private final String authUrl = "AUTH_URL";
    /**
     * Поле секретного ключа приложения
     */
    private final String clientSecret = "CLIENT_SECRET";
    /**
     * Поле ключа доступа к vk api
     */
    private final String accessToken = "ACCESS_TOKEN";

    /**
     * Метод создающий пользователя приложения vk
     *
     * @return нового пользователя приложения vk
     */
    @Override
    public ServiceActor createAppActor() {
        final int serviceActorId = -1;
        return new ServiceActor(serviceActorId, clientSecret, accessToken);
    }

    /**
     * Метод создающий нового пользователя vk
     *
     * @param userIdInBotSystem - id пользователя в системе
     * @return новый пользователь авторизовавшийся в vk
     */
    @Override
    public BotUser createBotUser(String userIdInBotSystem) {
        final int botUserId = 1;
        return new BotUser(botUserId, accessToken, userIdInBotSystem);
    }

    /**
     * Метод возвращающий ссылку для авторизации пользователя
     *
     * @return ссылку на страницу с авторизацией
     */
    @Override
    public String getAuthUrl() {
        return authUrl;
    }
}

/**
 * Класс тестового класса для взаимодействия с группами vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class TestVkGroups extends AbstractVkGroups {
    /**
     * Поле {@code map} в котором хранятся группы имеющие место в тестовой реализации
     */
    private final Map<String, List<Group>> testVkGroupsMap = new HashMap<>();


    /**
     * Метод получающий список найденных по подстроке групп
     *
     * @param userReceivedGroupName строка содержащая название группы
     * @param userCallingMethod     пользователь вызвавший метод
     * @return список групп, которые нашлись по некоторой подстроке
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     */
    @Override
    public List<Group> searchGroups(String userReceivedGroupName, BotUser userCallingMethod) throws NoGroupException, SocialNetworkException {

        if (userCallingMethod.getId() != 1) {
            throw new SocialNetworkException("Illegal user state");
        }

        List<Group> groups = testVkGroupsMap.get(userReceivedGroupName);

        if (groups == null) {
            throw new NoGroupException(userReceivedGroupName);
        }

        if (groups.isEmpty()) {
            throw new NoGroupException(userReceivedGroupName);
        }

        return groups;
    }

    /**
     * Метод возвращающий групп наиболее подходящую по названию к полученной подстроке
     *
     * @param userReceivedGroupName строка с названием группы полученная от пользователя
     * @param userCallingMethod     пользователь вызвавший метод
     * @return групп с наиболее близким к полученной подстроке названием
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     * @throws NoGroupException           возникает если не нашлась группа по заданной подстроке
     */
    @Override
    public Group searchGroup(String userReceivedGroupName, BotUser userCallingMethod) throws NoGroupException, SocialNetworkException {
        List<Group> groups = searchGroups(userReceivedGroupName, userCallingMethod);

        for (Group group : groups) {
            if (group.getScreenName().equals(userReceivedGroupName)) {
                return group;
            }
        }

        throw new NoGroupException(userReceivedGroupName);
    }
}

/**
 * Тестовый класс стены vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class TestVkWall extends AbstractVkWall {
    /**
     * Поле map экранного имени группы и постов в ней
     */
    private final Map<String, List<WallpostFull>> testVkWallMap = new HashMap<>();

    /**
     * Метод для получения {@code amountOfPosts} постов в виде строк
     *
     * @param groupScreenName   имя группы в социальной сети
     * @param amountOfPosts     кол-во постов которое необходимо получить
     * @param userCallingMethod пользователь вызвавший метод
     * @return текст указанного кол-ва постов, а также ссылки на другие группы, указанные в посте, и ссылки на посты
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     */
    @Override
    public List<String> getPostsStrings(String groupScreenName, int amountOfPosts, Actor userCallingMethod) throws SocialNetworkException, SocialNetworkAuthException {
        List<WallpostFull> posts = getPosts(groupScreenName, amountOfPosts, userCallingMethod);
        return VkPostsParser.parsePosts(posts);
    }

    /**
     * Метод для получения {@code amountOfPosts} постов в представлении vk
     *
     * @param groupScreenName   имя группы в социальной сети
     * @param amountOfPosts     кол-во запрашиваемых постов
     * @param userCallingMethod пользователь вызвавший метод
     * @return список постов в виде {@link WallpostFull}
     * @throws SocialNetworkException     возникает при ошибке обращения к vk api
     * @throws SocialNetworkAuthException возникает при ошибках аутентификации пользователя в vk
     */
    @Override
    public List<WallpostFull> getPosts(String groupScreenName, int amountOfPosts, Actor userCallingMethod) throws SocialNetworkException, SocialNetworkAuthException {

        if (amountOfPosts > 100) {
            throw new IllegalArgumentException("Кол-во запрашиваемых постов превышает кол-во доступных к получению");
        }

        if (Math.abs(userCallingMethod.getId()) != 1) {
            throw new SocialNetworkException("Illegal user state");
        }

        if (userCallingMethod.getAccessToken().equals("error")) {
            throw new SocialNetworkAuthException("Need to update token");
        }

        List<WallpostFull> findPosts = testVkWallMap.get(groupScreenName);

        if (findPosts == null) {
            return new ArrayList<>();
        }

        return testVkWallMap.get(groupScreenName).stream().limit(amountOfPosts).toList();
    }
}
