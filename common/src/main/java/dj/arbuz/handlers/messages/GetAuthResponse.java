package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotTextResponse;
import dj.arbuz.socialnetworks.vk.Vk;

import java.util.List;

public class GetAuthResponse implements MessageTelegramHandler {
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder AUTH_ERROR = MessageHandlerResponse.newBuilder()
                                                                                                                 .textMessage(BotTextResponse.AUTH_ERROR);

    private final Vk socialNetwork;

    public GetAuthResponse(Vk vk) {
        this.socialNetwork = vk;
    }

    @Override
    public MessageHandlerResponse sendMessage(String stringNull, String userSendResponseId) {
        String authURL = socialNetwork.getAuthUrl(userSendResponseId);

        if (authURL == null) {
            return AUTH_ERROR.build(List.of(userSendResponseId));
        }

        return MessageHandlerResponse.newBuilder()
                                     .textMessage( BotTextResponse.AUTH_GO_VIA_LINK + "[Ссылка]" + "(" + authURL + ")")
                                     .updateUser(socialNetwork.createBotUserAsync(userSendResponseId))
                                     .build(List.of(userSendResponseId));
    }
}
