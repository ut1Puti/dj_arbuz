package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;

import java.util.List;

public class HelpInfo implements MessageTelegramHandler {
    private static final MessageHandlerResponseBuilder HELP_INFO = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.HELP_INFO);

    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {
        return HELP_INFO.build(List.of(userSendResponseId));
    }
}
