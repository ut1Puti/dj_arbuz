package dj.arbuz.socialnetworks.socialnetwork.oAuth;

import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;

/**
 * Интерфейс для аутентификации пользователя в социальной сети
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public interface SocialNetworkAuth {
    /**
     * Метод для создания пользователя приложения социальной сети
     *
     * @param <S> параметр типа данных используемого в качестве пользователя приложения социальной сети
     * @return пользователя приложения социальной сети
     */
    <S> S createAppActor();

    /**
     * Метод создающий пользователя, имеющего id в системе
     *
     * @param userIdInBotSystem - id пользователя в системе
     * @param <U>               параметр типа данных используемого в качестве пользователя
     * @throws SocialNetworkException возникает при ошибках авторизации со стороны социально сети
     * @return пользователя содержащего id в социальной сети и в бот,
     * token - ключ доступа к api социальной сети с использованием аккаунта пользователя,
     * либо же {@code null} если не удается создать пользователя по какой-то причине
     */
    <U> U createBotUser(String userIdInBotSystem) throws SocialNetworkException;

    /**
     * Метод возвращающий ссылку для аутентификации в социальной сети
     *
     * @return ссылку для аутентификации в социальной сети, если ссылка по какой-то причине отсутствует возвращает {@code null}
     */
    String getAuthUrl(String userInSystemId);
}
