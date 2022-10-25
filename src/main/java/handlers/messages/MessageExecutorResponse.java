package handlers.messages;

import user.CreateUser;

import java.util.List;
import java.util.Objects;

/**
 * Класс ответов обработчика сообщений
 *
 * @author Кедровских Олег
 * @version 3.0
 * @see MessageExecutorResponseBuilder
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
     * Конструктор - создает экземпляр класса
     *
     * @param textMessage   текст сообщения отправленного от бота пользователю
     * @param updateUser    интерфейс для обновления пользователя
     * @param postsMessages список постов в виде строк
     */
    private MessageExecutorResponse(String textMessage, CreateUser updateUser, List<String> postsMessages) {
        this.textMessage = textMessage;
        this.updateUser = updateUser;
        this.postsMessages = postsMessages;
    }

    /**
     * Метод создающий класс {@code builder}, который позволяет создавать экземпляр класса с определенными параметрами
     *
     * @return {@code builder} класса ответов обработчика
     * @see MessageExecutorResponseBuilder
     */
    public static MessageExecutorResponseBuilder newBuilder() {
        return new MessageExecutorResponseBuilder();
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
     * Метод создающий хэш класса
     *
     * @return хэш текущего класса
     */
    @Override
    public int hashCode() {
        return Objects.hash(textMessage, postsMessages);
    }

    /**
     * Метод проверяющий равен ли {@code obj} экземпляру {@code MessageExecutorResponse}
     *
     * @param obj объект с которым сравнивается {@code MessageExecutorResponse}
     * @return {@code true} если равны, {@code false} если не равны
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MessageExecutorResponse messageExecutorResponse)) {
            return false;
        }

        return Objects.equals(textMessage, messageExecutorResponse.textMessage) &&
                Objects.equals(postsMessages, messageExecutorResponse.postsMessages);
    }

    /**
     * Класс {@code builder} для класса {@code MessageExecutorResponse}
     *
     * @author Кедровских Олег
     * @version 1.0
     * @see MessageExecutorResponse
     */
    public static class MessageExecutorResponseBuilder {
        /**
         * Поле текстового сообщения
         */
        private String textMessage;
        /**
         * Поле содержащее интерфейс для создания или обновления пользователя
         */
        private CreateUser updateUser;
        /**
         * Поле со списком постов в виде строк
         */
        private List<String> postsText;

        /**
         * Метод устанавливающий значение {@code testMessage}
         *
         * @param textMessage текстовое сообщения ответа
         * @return этот же {@code Builder}
         * @see MessageExecutorResponseBuilder#textMessage
         */
        public MessageExecutorResponseBuilder textMessage(String textMessage) {
            this.textMessage = textMessage;
            return this;
        }

        /**
         * Метод устанавливающий значение {@code updateUser}
         *
         * @param updateUser Поле содержащее интерфейс для создания или обновления пользователя
         * @return этот же {@code Builder}
         * @see MessageExecutorResponseBuilder#updateUser
         */
        public MessageExecutorResponseBuilder updateUser(CreateUser updateUser) {
            this.updateUser = updateUser;
            return this;
        }

        /**
         * Метод устанавливающий значение {@code postsText}
         *
         * @param postsText список постов в виде строк
         * @return этот же {@code Builder}
         * @see MessageExecutorResponseBuilder#postsText
         */
        public MessageExecutorResponseBuilder postsText(List<String> postsText) {
            this.postsText = postsText;
            return this;
        }

        /**
         * Метод строящий новый ответ обработчика
         *
         * @return новый экземпляр {@code MessageExecutorResponse}
         * @see MessageExecutorResponse#MessageExecutorResponse(String, CreateUser, List)
         */
        public MessageExecutorResponse build() {
            return new MessageExecutorResponse(textMessage, updateUser, postsText);
        }
    }
}
