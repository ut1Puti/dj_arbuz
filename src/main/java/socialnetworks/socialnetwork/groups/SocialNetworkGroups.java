package socialnetworks.socialnetwork.groups;

import com.vk.api.sdk.objects.groups.Group;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuthException;
import user.BotUser;

import java.util.List;

/**
 * Интерфейс для взаимодействия с группами в социальных сетях
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface SocialNetworkGroups<G, U> {
    /**
     * Метод для получения групп в социальной сети, по подстроке полученной от пользователя
     *
     * @param userReceivedGroupName строка содержащая название группы
     * @param userCallingMethod пользователь вызвавший метод
     * @return список групп в социальной сети полученных по подстроке пользователя
     * @throws NoGroupException возникает если не нашлось групп по заданной подстроке
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     */
    List<G> searchGroups(String userReceivedGroupName, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;

    /**
     * Метод для получения группы в социальной сети, которая лучше всего подходит под переданную пользователем подстроку
     *
     * @param userReceivedGroupName строка с названием группы полученная от пользователя
     * @param userCallingMethod пользователь вызвавший метод
     * @return группу с наибольшим число подписчик, среди групп,
     * названия которых хотя бы на 50% совпадают с полученной от пользователя строкой
     * @throws NoGroupException возникает если не нашлось групп по полученной от пользователя строке,
     * либо если среди полученных групп ни одна, ни совпадает хотя бы на 50% с исходной строкой
     * @throws SocialNetworkException возникает при ошибках обращения к api социальной сети
     * @throws SocialNetworkAuthException возникает при ошибке аутентификации пользователя
     */
    G searchGroup(String userReceivedGroupName, U userCallingMethod)
            throws NoGroupException, SocialNetworkException;
}
