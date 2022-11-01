package handlers.messages;

import user.CreateUser;

import java.util.List;
import java.util.Objects;

/**
 * Класс ответов обработчика сообщений
 *
 * @author Кедровских Олег
 * @version 3.0
 * @see MessageHandlerResponseBuilder
 */
public class MessageHandlerResponse {
    /**
     * Поле id пользователя которому будет отправлен ответ
     */
    private final String userSendResponseId;
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
    private MessageHandlerResponse(String userSendResponseId, String textMessage, CreateUser updateUser, List<String> postsMessages) {
        this.userSendResponseId = userSendResponseId;
        this.textMessage = textMessage;
        this.updateUser = updateUser;
        this.postsMessages = postsMessages;
    }

    /**
     * Метод создающий класс {@code builder}, который позволяет создавать экземпляр класса с определенными параметрами
     *
     * @return {@code builder} класса ответов обработчика
     * @see MessageHandlerResponseBuilder
     */
    public static MessageHandlerResponseBuilder newBuilder() {
        return new MessageHandlerResponseBuilder();
    }

    /**
     * Метод для получения id пользователя, которому будет отправлено сообщение
     *
     * @return строку, содержащую id пользователя, которому будет отправлен ответ
     */
    public String getUserSendResponseId() {
        return userSendResponseId;
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
     * @return {@code true} - если есть посты, {@code false} - если нет постов
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
        return Objects.hash(userSendResponseId, textMessage, postsMessages);
    }

    /**
     * Метод проверяющий равен ли {@code obj} экземпляру {@code MessageHandlerResponse}
     *
     * @param obj объект с которым сравнивается {@code MessageHandlerResponse}
     * @return {@code true} если равны, {@code false} если не равны
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MessageHandlerResponse otherMessageHandlerResponse)) {
            return false;
        }

        return  Objects.equals(userSendResponseId, otherMessageHandlerResponse.userSendResponseId) &&
                Objects.equals(textMessage, otherMessageHandlerResponse.textMessage) &&
                Objects.equals(postsMessages, otherMessageHandlerResponse.postsMessages);
    }

    /**
     * Класс {@code builder} для класса {@code MessageHandlerResponse}
     *
     * @author Кедровских Олег
     * @version 1.0
     * @see MessageHandlerResponse
     */
    public static class MessageHandlerResponseBuilder {
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
        private List<String> postsMessages;

        /**
         * Метод устанавливающий значение {@code testMessage}
         *
         * @param textMessage текстовое сообщения ответа
         * @return этот же {@code Builder}
         * @see MessageHandlerResponseBuilder#textMessage
         */
        public MessageHandlerResponseBuilder textMessage(String textMessage) {
            this.textMessage = textMessage;
            return this;
        }

        /**
         * Метод устанавливающий значение {@code updateUser}
         *
         * @param updateUser Поле содержащее интерфейс для создания или обновления пользователя
         * @return этот же {@code Builder}
         * @see MessageHandlerResponseBuilder#updateUser
         */
        public MessageHandlerResponseBuilder updateUser(CreateUser updateUser) {
            this.updateUser = updateUser;
            return this;
        }

        /**
         * Метод устанавливающий значение {@code postsText}
         *
         * @param postsText список постов в виде строк
         * @return этот же {@code Builder}
         * @see MessageHandlerResponseBuilder#postsMessages
         */
        public MessageHandlerResponseBuilder postsText(List<String> postsText) {
            this.postsMessages = postsText;
            return this;
        }

        /**
         * Метод строящий новый ответ обработчика
         *
         * @return новый экземпляр {@code MessageExecutorResponse}
         * @see MessageHandlerResponse#MessageHandlerResponse(String, String, CreateUser, List)
         */
        public MessageHandlerResponse build(String userSendResponseId) {
            return new MessageHandlerResponse(userSendResponseId, textMessage, updateUser, postsMessages);
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
         * Метод проверяющий равен ли {@code obj} экземпляру {@code MessageHandlerResponseBuilder}
         *
         * @param obj объект с которым сравнивается {@code MessageHandlerResponseBuilder}
         * @return {@code true} если равны, {@code false} если не равны
         */
        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }

            if (!(obj instanceof MessageHandlerResponseBuilder otherMessageHandlerResponseBuilder)) {
                return false;
            }

            return  Objects.equals(textMessage, otherMessageHandlerResponseBuilder.textMessage) &&
                    Objects.equals(postsMessages, otherMessageHandlerResponseBuilder.postsMessages);
        }
    }
}
