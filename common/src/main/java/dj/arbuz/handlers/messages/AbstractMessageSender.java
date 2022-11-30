package dj.arbuz.handlers.messages;

import dj.arbuz.BotMessageExecutable;
import dj.arbuz.BotTextResponse;
import dj.arbuz.database.UserBase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import dj.arbuz.user.BotUser;

import java.util.concurrent.ExecutionException;
import java.util.List;

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
     * Поле хранилища пользователей
     *
     * @see UserBase
     */
    private final UserBase userStorage;

    /**
     * Реализация метод, который отправляет сообщения пользователям
     *
     * @param userSendResponse ответ бота, который необходимо отправить пользователю
     */
    @Override
    public void sendResponse(MessageHandlerResponse userSendResponse) {
        List<String> usersSendMessageId = userSendResponse.getUsersSendResponseId();

        if (userSendResponse.hasTextMessage()) {
            for (String userSendMessageId : usersSendMessageId) {
                messageSender.send(userSendMessageId, userSendResponse.getTextMessage());
            }
        }

        if (userSendResponse.hasAdditionalMessage()) {
            try {
                for (String userSendMessageId : usersSendMessageId) {
                    messageSender.send(userSendMessageId, userSendResponse.getAdditionalMessage().get());
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println(e.getMessage());
            }
        }

        if (userSendResponse.hasPostsMessages()) {
            for (String postMessage : userSendResponse.getPostsMessages()) {
                for (String userSendMessageId : usersSendMessageId) {
                    messageSender.send(userSendMessageId, postMessage);
                }
            }
        }

        if (userSendResponse.hasUpdateUser()) {
            BotUser currentUser = null;
            try {
                currentUser = userSendResponse.getUpdateUser().get();
            } catch (InterruptedException | ExecutionException ignored) {
            }

            assert usersSendMessageId.size() == 1;

            if (currentUser == null) {
                messageSender.send(usersSendMessageId.get(0), BotTextResponse.AUTH_ERROR);
            } else if (userStorage.addUser(usersSendMessageId.get(0), currentUser)) {
                messageSender.send(usersSendMessageId.get(0), BotTextResponse.AUTH_SUCCESS);
            } else {
                messageSender.send(usersSendMessageId.get(0), BotTextResponse.AUTH_ERROR);
            }

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
