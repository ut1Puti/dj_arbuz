package handlers.messages;

import user.CreateUser;

import java.util.List;

/**
 * Класс ответов обработчика сообщений
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public class MessageHandlerResponse {
    /**
     * Поле текстового сообщения
     */
    private String textMessage = null;
    /**
     * Поле содержащее интерфейс для создания или обновления пользователя
     */
    private CreateUser updateUser = null;
    /**
     * Поле со списком постов в виде строк
     */
    private List<String> postsMessages = null;

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage - текстовое сообщение
     * @param updateUser  - интерфейс для обновления или создания пользователя
     * @param postsMessages - посты в виде строки
     */
    public MessageHandlerResponse(String textMessage, CreateUser updateUser, List<String> postsMessages) {
        this.textMessage = textMessage;
        this.updateUser = updateUser;
        this.postsMessages = postsMessages;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage - текстовое сообщение
     * @param updateUser  - интерфейс для обновления или создания пользователя
     */
    public MessageHandlerResponse(String textMessage, CreateUser updateUser) {
        this.textMessage = textMessage;
        this.updateUser = updateUser;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage - текстовое сообщение
     * @param postsMessages - посты в виде строки
     */
    public MessageHandlerResponse (String textMessage, List<String> postsMessages) {
        this.textMessage = textMessage;
        this.postsMessages = postsMessages;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param updateUser - интерфейс для обновления или создания пользователя
     * @param postsMessages - посты в виде строки
     */
    public MessageHandlerResponse(CreateUser updateUser, List<String> postsMessages) {
        this.updateUser = updateUser;
        this.postsMessages = postsMessages;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage - текстовое сообщение
     */
    public MessageHandlerResponse(String textMessage) {
        this.textMessage = textMessage;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param updateUser - интерфейс для обновления или создания пользователя
     */
    public MessageHandlerResponse(CreateUser updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param postsMessages - посты в виде строки
     */
    public MessageHandlerResponse(List<String> postsMessages) {
        this.postsMessages = postsMessages;
    }

    /**
     * Метод проверяющий наличие текстового сообщения
     *
     * @return наличие текстовое сообщение
     */
    public boolean hasTextMessage() {
        return textMessage != null;
    }

    /**
     * Метод возвращающий текстовое сообщение
     *
     * @return текстовое сообщение
     */
    public String getTextMessage() {
        return textMessage;
    }

    /**
     * Метод проверяющий наличие постов в ответе
     *
     * @return true - если есть посты
     * false - если нет постов
     */
    public boolean hasPostsMessages() {

        if (postsMessages == null) {
            return false;
        }

        return !postsMessages.isEmpty();
    }

    /**
     * Метод возвращающий посты из ответа
     *
     * @return список постов
     */
    public List<String> getPostsMessages() {
        return postsMessages;
    }

    /**
     * Метод проверяющий наличие интерфейса для обновления или создания пользователя
     *
     * @return наличие интерфейса для обновления или создания пользователя
     */
    public boolean hasUpdateUser() {
        return updateUser != null;
    }

    /**
     * Метод возвращающий интерфейс для обновления или создания пользователя
     *
     * @return интерфейс для обновления или создания пользователя
     */
    public CreateUser getUpdateUser() {
        return updateUser;
    }

    /**
     * Метод объединяющий сообщения разных ответов
     *
     * @param anotherHandlerResponse - другой ответ
     * @return ответ вызвавший этот метод, с добавленным сообщением другого ответа
     */
    MessageHandlerResponse appendTextMessage(MessageHandlerResponse anotherHandlerResponse) {
        if (anotherHandlerResponse != null && anotherHandlerResponse.textMessage != null) {
            this.textMessage = this.textMessage + "\n" + anotherHandlerResponse.textMessage;
        }
        return this;
    }
}
