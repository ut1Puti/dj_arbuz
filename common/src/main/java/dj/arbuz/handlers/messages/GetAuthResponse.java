package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.database.UserBase;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;
import dj.arbuz.socialnetworks.vk.AbstractVk;
import dj.arbuz.user.BotUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetAuthResponse implements MessageTelegramHandler {
    private static final MessageHandlerResponseBuilder AUTH_ERROR = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.AUTH_ERROR);

    private final AbstractVk vk;
    private final UserBase usersBase;

    public GetAuthResponse(AbstractVk vk, UserBase usersBase) {
        this.vk = vk;
        this.usersBase = usersBase;
    }

    @Override
    public MessageHandlerResponse sendMessage(String stringNull, String userSendResponseId) {
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
