package socialnetworks.vk;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.objects.wall.WallpostFull;
import loaders.gson.GsonLoader;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.vk.wall.AbstractVkWall;
import socialnetworks.vk.wall.VkPostsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Тестовый класс стены vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class VkWallMock extends AbstractVkWall {
    /**
     * Поле map экранного имени группы и постов в ней
     */
    private final Map<String, List<WallpostFull>> testVkWallMap = new HashMap<>();

    VkWallMock(String testWallDataJsonFilePath) {
        GsonLoader<WallpostList> jsonLoader = new GsonLoader<>(WallpostList.class);
        try {
            WallpostList wallPosts = jsonLoader.loadFromJson(testWallDataJsonFilePath);
            testVkWallMap.put("sqwore", wallPosts);
            testVkWallMap.put("no groups test", new ArrayList<>());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

class WallpostList extends ArrayList<WallpostFull> {
}
