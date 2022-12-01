package dj.arbuz.socialnetworks.socialnetwork.oAuth;

import java.util.List;

/**
 * Интерфейс для аутентификации пользователя в социальной сети
 *
 * @param <S> параметр типа данных используемого в качестве пользователя приложения социальной сети
 * @param <U> параметр типа данных используемого в качестве пользователя
 * @author Кедровских Олег
 * @version 1.0
 */
public interface SocialNetworkAuth<S, U, GU> {
    /**
     * Метод для создания пользователя приложения социальной сети
     *
     * @return пользователя приложения социальной сети
     */
    S createAppActor();

    /**
     * Метод создающий пользователя, имеющего id в системе
     *
     * @param userIdInBotSystem - id пользователя в системе
     * @return пользователя содержащего id в социальной сети и в бот,
     * token - ключ доступа к api социальной сети с использованием аккаунта пользователя,
     * либо же {@code null} если не удается создать пользователя по какой-то причине
     */
    U createBotUser(String userIdInBotSystem);

    /**
     * Метод возвращающий ссылку для аутентификации в социальной сети
     *
     * @return ссылку для аутентификации в социальной сети, если ссылка по какой-то причине отсутствует возвращает {@code null}
     */
    String getAuthUrl(String userInSystemId);

    /**
     *
     *
     * @param adminGroupsId
     * @return
     */
    String getGroupAuthUrl(List<String> adminGroupsId);

    List<GU> createGroupActor(List<String> groupsId);
}
