package handlers.messages;

import user.CreateUser;

import java.util.List;

/**
 * Класс ответов обработчика сообщений
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public class MessageExecutorResponse {
    /**
     * Поле текстового сообщения
     */
    private final String textMessage;
    /**
     * Поле содержащее интерфейс для создания или обновления пользователя
     */
    private final CreateUser updateUser;
    /**
     * Поле со списком постов в виде строк
     */
    private final List<String> postsMessages;
    /**
     * Поле флага о необходимости остановить бота
     */
    private final boolean stop;

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage   текстовое сообщение
     * @param updateUser    интерфейс для обновления или создания пользователя
     * @param postsMessages посты в виде строки
     * @param stop          флаг сообщающий нужно ли прекратить работу бота
     */
    MessageExecutorResponse(String textMessage, CreateUser updateUser, List<String> postsMessages, boolean stop) {
        this.textMessage = textMessage;
        this.updateUser = updateUser;
        this.postsMessages = postsMessages;
        this.stop = stop;
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage   текстовое сообщение
     * @param updateUser    интерфейс для обновления или создания пользователя
     * @param postsMessages посты в виде строки
     */
    MessageExecutorResponse(String textMessage, CreateUser updateUser, List<String> postsMessages) {
        this(textMessage, updateUser, postsMessages, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage текстовое сообщение
     * @param updateUser  интерфейс для обновления или создания пользователя
     */
    MessageExecutorResponse(String textMessage, CreateUser updateUser) {
        this(textMessage, updateUser, null, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage   текстовое сообщение
     * @param postsMessages посты в виде строки
     */
    MessageExecutorResponse(String textMessage, List<String> postsMessages) {
        this(textMessage, null, postsMessages, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param updateUser    интерфейс для обновления или создания пользователя
     * @param postsMessages посты в виде строки
     */
    MessageExecutorResponse(CreateUser updateUser, List<String> postsMessages) {
        this(null, updateUser, postsMessages, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage текстовое сообщение
     * @param stop        флаг сообщающий нужно ли прекратить работу бота
     */
    MessageExecutorResponse(String textMessage, boolean stop) {
        this(textMessage, null, null, stop);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param textMessage текстовое сообщение
     */
    MessageExecutorResponse(String textMessage) {
        this(textMessage, null, null, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param updateUser интерфейс для обновления или создания пользователя
     */
    MessageExecutorResponse(CreateUser updateUser) {
        this(null, updateUser, null, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param postsMessages посты в виде строки
     */
    MessageExecutorResponse(List<String> postsMessages) {
        this(null, null, postsMessages, false);
    }

    /**
     * Конструктор - создание объекта с определенными параметрами
     *
     * @param stop флаг сообщающий нужно ли прекратить работу бота
     */
    MessageExecutorResponse(boolean stop) {
        this(null, null, null, stop);
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
     * Метод проверяющий, надо ли прекратить работу бота
     *
     * @return значение флага {@code stop}
     */
    public boolean isBotStopped() {
        return stop;
    }
}
