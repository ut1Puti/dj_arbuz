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
abstract class MessageSender {
    /**
     * Поле бота, от имени которого будут отправлены сообщения
     *
     * @see BotMessageExecutable
     */
    private final BotMessageExecutable responseSendingBot;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param responseSendingBot бот, от имени которого будет отправлено сообщение
     */
    protected MessageSender(BotMessageExecutable responseSendingBot) {
        this.responseSendingBot = responseSendingBot;
    }

    /**
     * Реализация метод, который отправляет сообщения пользователям
     *
     * @param response ответ бота, который необходимо отправить пользователю
     * @param userBase база данных пользователей
     */
    public void executeMessage(MessageHandlerResponse response, UserStorage userBase) {
        String userSendResponseId = response.getUserSendResponseId();

        if (response.hasTextMessage()) {
            responseSendingBot.execute(userSendResponseId, response.getTextMessage());
        }

        if (response.hasPostsMessages()) {
            for (String postMessage : response.getPostsMessages()) {
                responseSendingBot.execute(userSendResponseId, postMessage);
            }
        }

        if (response.hasUpdateUser()) {
            BotUser currentUser = null;
            try {
                currentUser = response.getUpdateUser().get();
            } catch (InterruptedException | ExecutionException ignored) {
            }

            if (currentUser == null) {
                responseSendingBot.execute(userSendResponseId, BotTextResponse.AUTH_ERROR);
                return;
            }

            responseSendingBot.execute(userSendResponseId, BotTextResponse.AUTH_SUCCESS);
            userBase.addInfoUser(userSendResponseId, currentUser);
        }

    }

    /**
     * Метод для отправки единичного сообщения пользователю
     *
     * @param userSendResponseId id пользователя, которому будет отправлено сообщение
     * @param userSendText текст сообщения, которое будет отправлено пользователю
     */
    public void executeSingleMessage(String userSendResponseId, String userSendText) {
        responseSendingBot.execute(userSendResponseId, userSendText);
    }
}
