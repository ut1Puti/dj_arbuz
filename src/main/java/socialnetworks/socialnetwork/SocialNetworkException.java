package socialnetworks.socialnetwork;

/**
 * Класс ошибок при работе с api социальной сети
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class SocialNetworkException extends Exception{
    /**
     * Конструктор - создает экземпляр класса
     *
     * @param message сообщение с информацией об ошибке
     */
    public SocialNetworkException(String message) {
        super(message);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param message сообщение с информацией об ошибке
     * @param throwable другое исключение
     */
    public SocialNetworkException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param throwable другое исключение
     */
    public SocialNetworkException(Throwable throwable) {
        super(throwable);
    }
}
