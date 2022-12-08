package dj.arbuz.handlers.messages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Класс ответов обработчика сообщений
 *
 * @author Кедровских Олег
 * @version 3.0
 * @see MessageHandlerResponseBuilder
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = {"additionalMessage"})
public final class MessageHandlerResponse {
    /**
     * Поле id пользователя которому будет отправлен ответ
     */
    private final List<String> usersSendResponseId;
    /**
     * Поле текстового сообщения
     */
    @Getter
    private final String textMessage;
    @Getter
    private final Future<String> additionalMessage;
    /**
     * Поле со списком постов в виде строк
     */
    private final List<String> postsMessages;

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
     * Метод возвращающий всех пользователей, которым будут отправлены сообщение
     *
     * @return список id пользователей, которым будет отправлено сообщение
     */
    public List<String> getUsersSendResponseId() {
        return List.copyOf(usersSendResponseId);
    }

    public boolean hasAdditionalMessage() {
        return additionalMessage != null;
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
        return List.copyOf(postsMessages);
    }

    /**
     * Класс {@code builder} для класса {@code MessageHandlerResponse}
     *
     * @author Кедровских Олег
     * @version 1.0
     * @see MessageHandlerResponse
     */
    @NoArgsConstructor
    @EqualsAndHashCode(exclude = {"additionalMessage"})
    public static class MessageHandlerResponseBuilder {
        /**
         * Поле текстового сообщения
         */
        private String textMessage;
        /**
         * Поле со списком постов в виде строк
         */
        private List<String> postsMessages;
        private Future<String> additionalMessage;

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

        public MessageHandlerResponseBuilder additionalMessage(Future<String> additionalMessage) {
            this.additionalMessage = additionalMessage;
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
         * @see MessageHandlerResponse#MessageHandlerResponse(List, String, Future, List)
         */
        public MessageHandlerResponse build(List<String> userSendResponseId) {
            return new MessageHandlerResponse(userSendResponseId, textMessage, additionalMessage, postsMessages);
        }
    }
}
