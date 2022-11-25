package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotMessageExecutable;
import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.database.UserBase;
import dj.arbuz.database.local.UserStorage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import dj.arbuz.user.BotUser;

import java.util.concurrent.ExecutionException;

/**
 * Класс-отправитель сообщений пользователям
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractMessageSender implements MessageSender {
    /**
     * Потребитель принимающий на вход id пользователя и строку содержащую сообщение,
     * которую необходимо отправить пользователю
     */
    private final BotMessageExecutable messageSender;
    /**
     * Поле хранилища пользователей
     *
     * @see UserStorage
     */
    private final UserBase userStorage;

    /**
     * Реализация метод, который отправляет сообщения пользователям
     *
     * @param userSendResponse ответ бота, который необходимо отправить пользователю
     */
    @Override
    public void sendResponse(MessageHandlerResponse userSendResponse) {
        String userSendResponseId = userSendResponse.getUserSendResponseId();

        if (userSendResponse.hasTextMessage()) {
            messageSender.send(userSendResponseId, userSendResponse.getTextMessage());
        }

        if (userSendResponse.hasPostsMessages()) {
            for (String postMessage : userSendResponse.getPostsMessages()) {
                messageSender.send(userSendResponseId, postMessage);
            }
        }

        if (userSendResponse.hasUpdateUser()) {
            BotUser currentUser = null;
            try {
                currentUser = userSendResponse.getUpdateUser().get();
            } catch (InterruptedException | ExecutionException ignored) {
            }

            if (currentUser == null) {
                messageSender.send(userSendResponseId, BotTextResponse.AUTH_ERROR);
            }else if (userStorage.addUser(userSendResponseId, currentUser)) {
                messageSender.send(userSendResponseId, BotTextResponse.AUTH_SUCCESS);
            } else {
                messageSender.send(userSendResponseId, BotTextResponse.AUTH_ERROR);
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
