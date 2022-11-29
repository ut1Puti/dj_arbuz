package dj.arbuz.socialnetworks.socialnetwork.oAuth;

import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;

/**
 * Класс ошибок аутентификации в социальных сетях
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class SocialNetworkAuthException extends SocialNetworkException {
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param message сообщение с информацией об ошибке
     */
    public SocialNetworkAuthException(String message) {
        super(message);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param message message сообщение с информацией об ошибке
     * @param throwable другое исключение
     */
    public SocialNetworkAuthException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param throwable другое исключение
     */
    public SocialNetworkAuthException(Throwable throwable) {
        super(throwable);
    }
}
