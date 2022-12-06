package dj.arbuz;

import dj.arbuz.handlers.messages.MessageHandlerResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Класс-отправитель сообщений пользователям
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMessageSender implements MessageSender {
    /**
     * Потребитель принимающий на вход id пользователя и строку содержащую сообщение,
     * которую необходимо отправить пользователю
     */
    private final BotMessageExecutable messageSender;

    /**
     * Реализация метод, который отправляет сообщения пользователям
     *
     * @param userSendResponse ответ бота, который необходимо отправить пользователю
     */
    @Override
    public void sendResponse(MessageHandlerResponse userSendResponse) {
        List<String> usersSendMessageId = userSendResponse.getUsersSendResponseId();

        if (userSendResponse.hasTextMessage()) {
            sendTextMessage(userSendResponse.getTextMessage(), usersSendMessageId);
        }

        if (userSendResponse.hasPostsMessages()) {
            sendPostsMessage(userSendResponse.getPostsMessages(), usersSendMessageId);
        }

        if (userSendResponse.hasAdditionalMessage()) {
            sendAdditionalMessage(userSendResponse, usersSendMessageId);
        }
    }

    protected void sendTextMessage(String textMessage, List<String> usersSendMessageId) {
        for (String userSendMessageId : usersSendMessageId) {
            messageSender.send(userSendMessageId, textMessage);
        }
    }

    protected void sendPostsMessage(List<String> postsMessage, List<String> usersSendMessageId) {
        for (String postMessage : postsMessage) {
            for (String userSendMessageId : usersSendMessageId) {
                messageSender.send(userSendMessageId, postMessage);
            }
        }
    }

    protected void sendAdditionalMessage(MessageHandlerResponse userSendResponse, List<String> usersSendMessageId) {
        String userSendMessage = getAdditionalMessageFromResponse(userSendResponse);
        for (String userSendMessageId : usersSendMessageId) {
            messageSender.send(userSendMessageId, userSendMessage);
        }
    }

    /**
     * Метод получающий дополнительное сообщение для пользователя
     *
     * @param userSendHandlerResponse {@code CompletableFuture} который возвращает текстовое сообщение для пользователя
     * @return строку соержащую сообщение, которое будет отправлено пользователю
     */
    private String getAdditionalMessageFromResponse(MessageHandlerResponse userSendHandlerResponse) {
        try {
            return userSendHandlerResponse.getAdditionalMessage().get(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            return BotTextResponse.TIME_EXPIRED;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e.getMessage());
            return BotTextResponse.HANDLER_ERROR;
        }
    }

    /**
     * Метод для отправки единичного сообщения пользователю
     *
     * @param userSendResponseId id пользователя, которому будет отправлено сообщение
     * @param userSendText       текст сообщения, которое будет отправлено пользователю
     */
    @Override
    public void sendSingleMessage(String userSendResponseId, String userSendText) {
        messageSender.send(userSendResponseId, userSendText);
    }
}
