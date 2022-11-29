package dj.arbuz.socialnetworks.vk;

import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.wall.WallpostFull;
import dj.arbuz.database.GroupBase;
import org.junit.jupiter.api.Test;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.socialnetwork.groups.NoGroupException;
import dj.arbuz.socialnetworks.socialnetwork.groups.SubscribeStatus;
import dj.arbuz.user.BotUser;
import org.mockito.Mockito;

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
        String userReceivedGroupName = "Sqwore";
        assertEquals(VkConstants.VK_ADDRESS + "sqwore", vk.getGroupUrl(userReceivedGroupName, user));
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
        String userReceivedGroupName = "Sqwore";
        assertEquals("188069740", vk.getGroupId(userReceivedGroupName, user));
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
        Mockito.when(groupBase.addSubscriber("sqwore", userSystemId))
                .thenReturn(true);
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "Sqwore";
        assertEquals(SubscribeStatus.SUBSCRIBED, vk.subscribeTo(groupBase, userReceivedGroupName, user));
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
                () -> vk.subscribeTo(groupBase, userReceivedGroupName, user),
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
        String userReceivedGroupName = "Мемняя дыра, про мамку Щюклина";
        assertEquals(SubscribeStatus.GROUP_IS_CLOSED, vk.subscribeTo(groupBase, userReceivedGroupName, user));
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
        Mockito.when(groupBase.addSubscriber("abracadabra", userSystemId))
                .thenReturn(false);
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        groupBase.addSubscriber("sqwore", userSystemId);
        String userReceivedGroupName = "Sqwore";
        assertEquals(SubscribeStatus.ALREADY_SUBSCRIBED, vk.subscribeTo(groupBase, userReceivedGroupName, user));
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
        Mockito.when(groupBase.deleteSubscriber("sqwore", userSystemId))
                .thenReturn(true);
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "Sqwore";
        assertTrue(vk.unsubscribeFrom(groupBase, userReceivedGroupName, user));
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
                () -> vk.unsubscribeFrom(groupBase, userReceivedGroupName, user),
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
        Mockito.when(groupBase.deleteSubscriber("Мемняя дыра, про мамку Щюклина", userSystemId))
                .thenReturn(false);
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "Мемняя дыра, про мамку Щюклина";
        assertFalse(vk.unsubscribeFrom(groupBase, userReceivedGroupName, user));
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
        Mockito.when(groupBase.deleteSubscriber("sqwore", userSystemId))
                .thenReturn(false);
        BotUser user = vk.createBotUserAsync(userSystemId).get();
        String userReceivedGroupName = "Sqwore";
        assertFalse(vk.unsubscribeFrom(groupBase, userReceivedGroupName, user));
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
        String userReceivedGroupName = "Sqwore";
        List<String> notExpectedPostsStrings = new ArrayList<>();
        assertNotEquals(notExpectedPostsStrings, vk.getLastPostsAsStrings(userReceivedGroupName, 1, user));
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
        String userReceivedGroupName = "no groups test";
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
        String userReceivedGroupName = "Sqwore";
        List<WallpostFull> notExpectedPostsStrings = new ArrayList<>();
        assertNotEquals(notExpectedPostsStrings, vk.getLastPostsAsPosts(userReceivedGroupName, 1, user));
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
        String userReceivedGroupName = "no groups test";
        List<WallpostFull> expectedPostsStrings = new ArrayList<>();
        assertEquals(expectedPostsStrings, vk.getLastPostsAsPosts(userReceivedGroupName, 1, user));
    }
}
