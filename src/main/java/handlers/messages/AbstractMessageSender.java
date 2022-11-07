package handlers.messages;

import bots.BotMessageExecutable;
import bots.BotTextResponse;
import database.UserStorage;
import user.BotUser;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * Класс-отправитель сообщений пользователям
 *
 * @author Кедровских Олег
 * @version 1.0
 */
abstract class AbstractMessageSender implements MessageSender {
    /**
     *
     */
    private final BiConsumer<String, String> consumer;
    /**
     * Поле хранилища пользователей
     *
     * @see UserStorage
     */
    private final UserStorage userStorage;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param consumer реализация интерфейса, логика которой является логикой отправки сообщения пользователю
     * @param userStorage хранилище пользователей
     */
    protected AbstractMessageSender(BiConsumer<String, String> consumer, UserStorage userStorage) {
        this.consumer = consumer;
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
            consumer.accept(userSendResponseId, userSendResponse.getTextMessage());
        }

        if (userSendResponse.hasPostsMessages()) {
            for (String postMessage : userSendResponse.getPostsMessages()) {
                consumer.accept(userSendResponseId, postMessage);
            }
        }

        if (userSendResponse.hasUpdateUser()) {
            BotUser currentUser = null;
            try {
                currentUser = userSendResponse.getUpdateUser().get();
            } catch (InterruptedException | ExecutionException ignored) {
            }

            if (currentUser == null) {
                consumer.accept(userSendResponseId, BotTextResponse.AUTH_ERROR);
                return;
            }

            consumer.accept(userSendResponseId, BotTextResponse.AUTH_SUCCESS);
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
        consumer.accept(userSendResponseId, userSendText);
    }
}
