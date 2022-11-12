package handlers.messages;

import bots.BotMessageExecutable;
import bots.BotTextResponse;
import database.UserBase;
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
     * Конструктор - создает экземпляр класса
     *
     * @param messageSender    реализация интерфейса, логика которой является логикой отправки сообщения пользователю
     * @param userStorage хранилище пользователей
     */
    protected AbstractMessageSender(BotMessageExecutable messageSender, UserBase userStorage) {
        this.messageSender = messageSender;
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
                return;
            }

            messageSender.send(userSendResponseId, BotTextResponse.AUTH_SUCCESS);
            userStorage.addInfoUser(userSendResponseId, currentUser);
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
