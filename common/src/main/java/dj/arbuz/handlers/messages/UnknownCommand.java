package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotTextResponse;

import java.util.List;

public class UnknownCommand implements MessageTelegramHandler{
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder UNKNOWN_COMMAND = MessageHandlerResponse.newBuilder()
                                                                                                                      .textMessage(BotTextResponse.UNKNOWN_COMMAND);
    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {
        return UNKNOWN_COMMAND.build(List.of(userSendResponseId));
    }
}
