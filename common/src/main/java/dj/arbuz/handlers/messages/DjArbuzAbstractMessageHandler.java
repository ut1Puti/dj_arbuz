package dj.arbuz.handlers.messages;

import dj.arbuz.BotTextResponse;
import dj.arbuz.handlers.messages.MessageHandlerResponse.MessageHandlerResponseBuilder;

/**
 * Класс обработки сообщения dj_arbuz'а
 *
 * @author Кедровских Олег
 * @version 1.0
 */
abstract class DjArbuzAbstractMessageHandler implements MessageHandler {
    /**
     * Поле сообщения бота start
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#START
     */
    protected static final MessageHandlerResponseBuilder START = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.START);
    /**
     * Поле help сообщения бота
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#HELP_INFO
     */
    protected static final MessageHandlerResponseBuilder HELP_INFO = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.HELP_INFO);
    /**
     * Поле сообщения при ошибке аутентификации пользователя
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#AUTH_ERROR
     */
    protected static final MessageHandlerResponseBuilder AUTH_ERROR = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.AUTH_ERROR);
    /**
     * Поле сообщения о получении неизвестной команды
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#UNKNOWN_COMMAND
     */
    protected static final MessageHandlerResponseBuilder UNKNOWN_COMMAND = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.UNKNOWN_COMMAND);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не аутентифицировался в социальной сети
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_AUTHED_USER
     */
    protected static final MessageHandlerResponseBuilder NOT_AUTHED_USER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_AUTHED_USER);
    /**
     * Поле сообщения с текстом, в котором говориться, что не нашлось постов в группе
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_POSTS_IN_GROUP
     */
    protected static final MessageHandlerResponseBuilder NO_POSTS_IN_GROUP = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_POSTS_IN_GROUP);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь быд отписан от группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#UNSUBSCRIBED
     */
    protected static final MessageHandlerResponseBuilder UNSUBSCRIBED = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.UNSUBSCRIBED);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не был подписчиком группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NOT_SUBSCRIBER
     */
    protected static final MessageHandlerResponseBuilder NOT_SUBSCRIBER = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NOT_SUBSCRIBER);
    /**
     * Поле сообщения с текстом, в котором говориться, что пользователь не подписан ни на одну группу
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#NO_SUBSCRIBED_GROUPS
     */
    protected static final MessageHandlerResponseBuilder NO_SUBSCRIBED_GROUPS = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.NO_SUBSCRIBED_GROUPS);
    /**
     * Поле сообщения в котором сообщается, что пользователь не является админом группы
     *
     * @see MessageHandlerResponse.MessageHandlerResponseBuilder
     * @see BotTextResponse#IS_NOT_ADMIN
     */
    protected static final MessageHandlerResponseBuilder IS_NOT_ADMIN = MessageHandlerResponse.newBuilder()
            .textMessage(BotTextResponse.IS_NOT_ADMIN);

    /**
     * Метод создающий ответ при неправильном параметре
     *
     * @param commandName     название команды
     * @param illegalArgument неправильный аргумент
     * @return ответ
     */
    protected MessageHandlerResponseBuilder createIllegalArgumentMessage(String commandName, String illegalArgument) {
        return MessageHandlerResponse.newBuilder()
                .textMessage(commandName + BotTextResponse.ILLEGAL_ARGUMENT + illegalArgument);
    }

    /**
     * Метод создающий ответ при отсутствии необходимого параметра
     *
     * @param commandName     название команды
     * @param exampleArgument пример необходимого аргумента
     * @return ответ
     */
    protected MessageHandlerResponseBuilder createNoArgumentMessage(String commandName, String exampleArgument) {
        return MessageHandlerResponse.newBuilder()
                .textMessage(commandName + BotTextResponse.NO_ARGUMENT + exampleArgument);
    }
}
