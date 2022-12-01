package dj.arbuz.socialnetworks.vk;

import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.wall.WallpostFull;
<<<<<<<< HEAD:common/src/test/java/dj/arbuz/socialnetworks/vk/AbstractVkTests.java
import dj.arbuz.database.GroupBase;
========
import dj.arbuz.database.local.GroupsStorage;
import org.junit.jupiter.api.BeforeEach;
>>>>>>>> developTaskFour:src/test/java/dj/arbuz/socialnetworks/vk/AbstractVkTests.java
import org.junit.jupiter.api.Test;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.socialnetwork.groups.SubscribeStatus;
import dj.arbuz.user.BotUser;
<<<<<<<< HEAD:common/src/test/java/dj/arbuz/socialnetworks/vk/AbstractVkTests.java
import org.mockito.Mockito;
========
>>>>>>>> developTaskFour:src/test/java/dj/arbuz/socialnetworks/vk/AbstractVkTests.java

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
     * Поле хранилища подписок на группы
     */

    private final GroupBase groupBase = Mockito.mock(GroupBase.class);
    /**
     * Поле тестовой реализации vk
     */
    private final AbstractVk vk = new VkMock();

    /**
     * Метод тестирующий получение ссылки для аутентификации
     */
    @Test
    public void testGetAuthUrl() {
        assertEquals("AUTH_URL&state=a", vk.getAuthUrl("a"));
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
     */
    @Test
    public void testCreateUserAsync() {
        BotUser updateUser = vk.createBotUser(userSystemId);
        assertEquals(new BotUser(1, "ACCESS_TOKEN", userSystemId), updateUser);
    }

    /**
     * Метод проверяющий получение ссылки на существующую группу
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetExistGroupUrl() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertEquals(VkConstants.VK_ADDRESS + "sqwore", vk.getGroupUrl(userReceivedGroupName, user));
    }

    /**
     * Метод проверяющий получение ссылки на группу по подстроке, которой не соответствует ни одна группа среди доступных
     */
    @Test
    public void testGetNotExistGroupUrl() {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getGroupUrl(userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод проверяющий получение id группы, на существующую группу
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetExistGroupId() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertEquals("188069740", vk.getGroupId(userReceivedGroupName, user));
    }

    /**
     * Метод проверяющий получение id группы, которой не существует
     */
    @Test
    public void testGetNotExistGroupId() {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getGroupId(userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий подписку на существующую группу
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testSubscribeToNotSubscribedExistGroup() throws NoGroupException, SocialNetworkException {
        Mockito.when(groupBase.addSubscriber("sqwore", userSystemId))
                .thenReturn(true);
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertEquals(SubscribeStatus.SUBSCRIBED, vk.subscribeTo(groupBase, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий подписку на несуществующую группу
     */
    @Test
    public void testSubscribeToNotExistGroup() {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.subscribeTo(groupBase, userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий подписку на закрытую группу
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testSubscribeToClosedGroup() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Мемняя дыра, про мамку Щюклина";
        assertEquals(SubscribeStatus.GROUP_IS_CLOSED, vk.subscribeTo(groupBase, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий подписку на подписанную группу
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testSubscribeToSubscribedGroup() throws NoGroupException, SocialNetworkException {
        Mockito.when(groupBase.addSubscriber("abracadabra", userSystemId))
                .thenReturn(false);
        BotUser user = vk.createBotUser(userSystemId);
        groupBase.addSubscriber("sqwore", userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertEquals(SubscribeStatus.ALREADY_SUBSCRIBED, vk.subscribeTo(groupBase, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий отписку от существующей группу
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testUnsubscribeFromExistSubscribedGroup() throws NoGroupException, SocialNetworkException {
        Mockito.when(groupBase.deleteSubscriber("sqwore", userSystemId))
                .thenReturn(true);
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertTrue(vk.unsubscribeFrom(groupBase, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий отписки от несуществующей группы
     */
    @Test
    public void testUnsubscribeFromNotExistGroup() {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.unsubscribeFrom(groupBase, userReceivedGroupName, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий отписку от закрытой группы
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testUnsubscribeFromClosedGroup() throws NoGroupException, SocialNetworkException {
        Mockito.when(groupBase.deleteSubscriber("Мемняя дыра, про мамку Щюклина", userSystemId))
                .thenReturn(false);
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Мемняя дыра, про мамку Щюклина";
        assertFalse(vk.unsubscribeFrom(groupBase, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий отписки от существующей не подписанной группы
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testUnsubscribeFromExistNotSubscribedGroup() throws NoGroupException, SocialNetworkException {
        Mockito.when(groupBase.deleteSubscriber("sqwore", userSystemId))
                .thenReturn(false);
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertFalse(vk.unsubscribeFrom(groupBase, userReceivedGroupName, user));
    }

    /**
     * Метод тестирующий получение постов в виде строк из существующей группы
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsStringsFromExistGroup() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        List<String> notExpectedPostsStrings = new ArrayList<>();
        assertNotEquals(notExpectedPostsStrings, vk.getLastPostsAsStrings(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение постов в виде строк из не существующей группы
     */
    @Test
    public void testGetLastPostsAsStringsFromNotExistGroup() {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getLastPostsAsStrings(userReceivedGroupName, 10, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий получение постов в виде строк, в группе в которой нет постов
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsStringsFromExistGroupWithNoPosts() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "no groups test";
        List<String> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsStrings(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение постов в виде {@code WallpostFull} из существующей группы
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsPostsFromExistGroup() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "Sqwore";
        List<WallpostFull> notExpectedPostsStrings = new ArrayList<>();
        assertNotEquals(notExpectedPostsStrings, vk.getLastPostsAsPosts(userReceivedGroupName, 1, user));
    }

    /**
     * Метод тестирующий получение постов в виде {@code WallpostFull} из не существующей группы
     */
    @Test
    public void testGetLastPostsAsPostsFromNotExistGroup() {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "abracadabra";
        assertThrows(NoGroupException.class,
                () -> vk.getLastPostsAsPosts(userReceivedGroupName, 10, user),
                "Группы с названием" + userReceivedGroupName + " не существует");
    }

    /**
     * Метод тестирующий получение постов в виде {@code WallpostFull} из группы в которой нет постов
     *
     * @throws NoGroupException возникает если группы по полученной от пользователя подстроке
     * @throws SocialNetworkException возникает при ошибке обращения к vk api
     */
    @Test
    public void testGetLastPostsAsPostsFromExistGroupWithNoPosts() throws NoGroupException, SocialNetworkException {
        BotUser user = vk.createBotUser(userSystemId);
        String userReceivedGroupName = "no groups test";
        List<WallpostFull> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsPosts(userReceivedGroupName, 1, user));
    }
}
