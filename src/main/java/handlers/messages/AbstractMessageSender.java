package handlers.messages;

import bots.BotMessageExecutable;
import bots.BotTextResponse;
import database.UserStorage;
import user.BotUser;

import java.util.concurrent.ExecutionException;

/**
 * Класс-отправитель сообщений пользователям
 *
 * @author Кедровских Олег
 * @version 1.0
 */
abstract class AbstractMessageSender implements MessageSender {
    /**
     * Поле бота, от имени которого будут отправлены сообщения
     *
     * @see BotMessageExecutable
     */
    private final BotMessageExecutable responseSendingBot;
    /**
     * Поле хранилища пользователей
     *
     * @see UserStorage
     */
    private final UserStorage userStorage;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param responseSendingBot бот, от имени которого будет отправлено сообщение
     * @param userStorage хранилище пользователей
     */
    protected AbstractMessageSender(BotMessageExecutable responseSendingBot, UserStorage userStorage) {
        this.responseSendingBot = responseSendingBot;
        this.userStorage = userStorage;
    }

    /**
     * Реализация метод, который отправляет сообщения пользователям
     *
     * @param userSendResponse ответ бота, который необходимо отправить пользователю
     */
    @Override
    public void sendResponse(MessageHandlerResponse userSendResponse) {
        String userSendResponseId = userSendResponse.getUserSendResponseId();

        if (userSendResponse.hasTextMessage()) {
            responseSendingBot.send(userSendResponseId, userSendResponse.getTextMessage());
        }

        if (userSendResponse.hasPostsMessages()) {
            for (String postMessage : userSendResponse.getPostsMessages()) {
                responseSendingBot.send(userSendResponseId, postMessage);
            }
        }

        if (userSendResponse.hasUpdateUser()) {
            BotUser currentUser = null;
            try {
                currentUser = userSendResponse.getUpdateUser().get();
            } catch (InterruptedException | ExecutionException ignored) {
            }

            if (currentUser == null) {
                responseSendingBot.send(userSendResponseId, BotTextResponse.AUTH_ERROR);
                return;
            }

            responseSendingBot.send(userSendResponseId, BotTextResponse.AUTH_SUCCESS);
            userStorage.addInfoUser(userSendResponseId, currentUser);
        }

    }

    /**
     * Метод для отправки единичного сообщения пользователю
     *
     * @param userSendResponseId id пользователя, которому будет отправлено сообщение
     * @param userSendText текст сообщения, которое будет отправлено пользователю
     */
    @Override
    public void sendSingleMessage(String userSendResponseId, String userSendText) {
        responseSendingBot.send(userSendResponseId, userSendText);
    }
}
