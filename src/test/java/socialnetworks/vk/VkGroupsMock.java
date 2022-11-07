package socialnetworks.vk;

import com.vk.api.sdk.objects.groups.Group;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.groups.NoGroupException;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import socialnetworks.vk.groups.AbstractVkGroups;
import user.BotUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс тестового класса для взаимодействия с группами vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class VkGroupsMock extends AbstractVkGroups {
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
