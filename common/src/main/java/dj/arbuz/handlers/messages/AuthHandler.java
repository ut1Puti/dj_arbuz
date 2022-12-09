package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.AbstractSocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetworkException;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Класс обработки команды /auth
 *
 * @author Щеголев Андрей
 * @version 1.0
 */
@RequiredArgsConstructor
public final class AuthHandler extends DjArbuzAbstractMessageHandler {
    /**
     * Поле класса для взаимодействия с api социальной сети
     *
     * @see SocialNetwork
     */
    private final AbstractVk vk;
    /**
     * Поле хранилища пользователей, аутентифицированный в социальной сети
     *
     * @see UserBase
     */
    private final UserBase usersBase;

    /**
     * Метод формирующий ответ на команду /auth
     *
     * @param userSendResponseId id пользователю, которому будет отправлен ответ
     * @return ответ на команду /auth
     * @see AbstractSocialNetwork#getAuthUrl(String)
     * @see AbstractSocialNetwork#createBotUser(String)
     * @see MessageHandlerResponse#newBuilder()
     * @see BotTextResponse#AUTH_GO_VIA_LINK
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#textMessage(String)
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#additionalMessage(Future)
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        if (!message.isBlank()) {
            return createIllegalArgumentMessage("/auth", message).build(List.of(userSendResponseId));
        }

        String authURL = vk.getAuthUrl(userSendResponseId);

        if (authURL == null) {
            return AUTH_ERROR.build(List.of(userSendResponseId));
        }

        CompletableFuture<String> createUserActorAnswer =
                CompletableFuture.supplyAsync(userAuthAnswerSupplier(userSendResponseId));
        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.AUTH_GO_VIA_LINK + authURL)
                .additionalMessage(createUserActorAnswer)
                .build(List.of(userSendResponseId));
    }

    /**
     * Создает {@code supplier} производящий ответ на результат авторизации пользователя
     *
     * @param userSendResponseId id пользователя, которого авторизуют
     * @return строку с ответом в зависимости от результата авторизации
     */
    private Supplier<String> userAuthAnswerSupplier(String userSendResponseId) {
        return () -> {
            try {
                return botUserAuth(vk.createBotUser(userSendResponseId));
            } catch (SocialNetworkException e) {
                return BotTextResponse.AUTH_ERROR;
            }
        };
    }

    /**
     * Метод добавляющий пользователя в базу данных
     *
     * @param botUserCreated созданный пользователь
     * @return строку содержащую ответ на то, был ли создан пользователь или нет
     */
    private String botUserAuth(BotUser botUserCreated) {
        if (botUserCreated == null) {
            return BotTextResponse.TIME_EXPIRED;
        } else if (usersBase.addUser(botUserCreated.getTelegramId(), botUserCreated)) {
            return BotTextResponse.AUTH_SUCCESS;
        } else {
            return BotTextResponse.AUTH_ERROR;
        }
    }
}
