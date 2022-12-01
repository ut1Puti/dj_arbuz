package dj.arbuz.handlers.messages;

public interface MessageTelegramHandler {
    public MessageHandlerResponse sendMessage(String userReceivedGroupName, String userSendResponseId);
}
