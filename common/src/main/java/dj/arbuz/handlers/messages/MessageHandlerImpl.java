package dj.arbuz.handlers.messages;

import dj.arbuz.database.GroupBase;
import dj.arbuz.database.UserBase;
import dj.arbuz.socialnetworks.vk.AbstractVk;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для обработки сообщений пользователей, а также создающий ответы на них
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 4.0
 * @see MessageHandler
 */
public final class MessageHandlerImpl extends DjArbuzAbstractMessageHandler {
    /**
     * Поле индекса команды
     */
    private static final int COMMAND_INDEX = 0;
    /**
     * Поле индекса аргумента
     */
    private static final int ARG_INDEX = 1;
    /**
     * Поле {@code map} с командами и их обработчиками
     */
    private final Map<String, MessageHandler> commandMap = new HashMap<>();
    /**
     * Поле обработчика неизвестной команды
     */
    private final MessageHandler unknownCommand = new UnknownCommandHandler();

    public MessageHandlerImpl(GroupBase groupBase, UserBase userBase, AbstractVk vk) {
        initCommandMap(groupBase, userBase, vk);
    }

    /**
     * Метод инициализирующий {@code map} командами и их обработчиками
     */
    private void initCommandMap(GroupBase groupBase, UserBase userBase, AbstractVk vk) {
        commandMap.put("/help", new HelpHandler());
        commandMap.put("/auth", new AuthHandler(vk, userBase));
        commandMap.put("/auth_as_admin", new AuthAsAdminHandler(groupBase, userBase, vk));
        commandMap.put("/subscribed", new GetUserSubscribedGroupsLinks(userBase, groupBase));
        commandMap.put("/stop", new StopHandler(userBase));
        commandMap.put("/link", new LinkHandler(userBase, vk));
        commandMap.put("/id", new IdHandler(userBase, vk));
        commandMap.put("/subscribe", new SubscribeToHandler(userBase, groupBase, vk));
        commandMap.put("/unsubscribe", new UnsubscribeFromHandler(userBase, groupBase, vk));
        commandMap.put("/five_posts", new FivePostsHandler(userBase, vk));
        commandMap.put("/post_to_all", new PostToAllHandler(userBase));
        commandMap.put("/post_to_group", new PostToGroupHandler(groupBase));
    }

    /**
     * Метод определяющий команды в сообщении пользователя и возвращающий ответ
     *
     * @param message            сообщение пользователя
     * @param userSendResponseId id пользователя в телеграмме
     * @return возвращает ответ на сообщение пользователя
     * @see MessageHandlerResponse
     * @see MessageHandlerImpl#isItNoArgCommand(String[])
     */
    @Override
    public MessageHandlerResponse handleMessage(String message, String userSendResponseId) {
        String[] commandAndArgs = message.split(" ", 2);

        MessageHandler messageHandler = commandMap.getOrDefault(commandAndArgs[COMMAND_INDEX], unknownCommand);

        if (isItNoArgCommand(commandAndArgs)) {
            return messageHandler.handleMessage("", userSendResponseId);
        } else {
            return messageHandler.handleMessage(commandAndArgs[ARG_INDEX], userSendResponseId);
        }

    }

    /**
     * Метод проверяет есть ли аргументы в полученной команде
     *
     * @param commandAndArgs - массив аргументов и команд
     * @return {@code true} - если нет аргументов, {@code false} - если есть аргументы
     */
    private static boolean isItNoArgCommand(String[] commandAndArgs) {
        return commandAndArgs.length == 1;
    }
}
