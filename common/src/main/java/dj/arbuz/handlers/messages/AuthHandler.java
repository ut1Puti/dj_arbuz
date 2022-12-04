package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.socialnetwork.AbstractSocialNetwork;
import dj.arbuz.socialnetworks.socialnetwork.SocialNetwork;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder#additionalMesage(CompletableFuture)
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        if (message != null) {
            return createIllegalArgumentMessage("/auth", message).build(List.of(userSendResponseId));
        }

        String authURL = vk.getAuthUrl(userSendResponseId);

        if (authURL == null) {
            return AUTH_ERROR.build(List.of(userSendResponseId));
        }

        CompletableFuture<String> createUserActorAnswer =
                CompletableFuture.supplyAsync(() -> botUserAuth(vk.createBotUser(userSendResponseId)));

        return MessageHandlerResponse.newBuilder()
                .textMessage(BotTextResponse.AUTH_GO_VIA_LINK + authURL)
                .additionalMesage(createUserActorAnswer)
                .build(List.of(userSendResponseId));
    }

    /**
     * Метод добавляющий пользователя в базу данных
     *
     * @param botUserCreated созданный пользователь
     * @return строку содержащую ответ на то, был ли создан пользователь или нет
     */
    private String botUserAuth(BotUser botUserCreated) {
        if (botUserCreated == null) {
            return BotTextResponse.AUTH_ERROR;
        } else if (usersBase.addUser(botUserCreated.getTelegramId(), botUserCreated)) {
            return BotTextResponse.AUTH_SUCCESS;
        } else {
            return BotTextResponse.AUTH_ERROR;
        }
    }
}
