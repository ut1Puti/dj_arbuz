package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;

import java.util.List;

public class UnknownCommand implements MessageTelegramHandler{
    private static final MessageHandlerResponseBuilder UNKNOWN_COMMAND = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.UNKNOWN_COMMAND);

    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {
        return UNKNOWN_COMMAND.build(List.of(userSendResponseId));
    }
}
