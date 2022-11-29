package dj.arbuz.handlers.messages;

import dj.arbuz.bots.BotTextResponse;

import java.util.List;

public class HelpInfo implements MessageTelegramHandler{
    private static final MessageHandlerResponse.MessageHandlerResponseBuilder HELP_INFO = MessageHandlerResponse.newBuilder()
                                                                                                                .textMessage(BotTextResponse.HELP_INFO);


    public HelpInfo() {}
    @Override
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId) {
        return HELP_INFO.build(List.of(userSendResponseId));
    }
}
