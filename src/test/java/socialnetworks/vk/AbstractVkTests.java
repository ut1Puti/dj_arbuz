package socialnetworks.vk;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.wall.WallpostFull;
import loaders.gson.GsonLoader;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.groups.NoGroupException;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.vk.groups.AbstractVkGroups;
import socialnetworks.vk.oAuth.AbstractVkAuth;
import socialnetworks.vk.wall.AbstractVkWall;
import socialnetworks.vk.wall.VkPostsParser;
import user.BotUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Класс тестирующий абстрактный класс vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class AbstractVkTests {
    private final AbstractVk vk = new TestVk();
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
    private final String clientSecret = "ClIENT_SECRET";
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

        return testVkWallMap.get(groupScreenName).stream().limit(amountOfPosts).toList();
    }
}
