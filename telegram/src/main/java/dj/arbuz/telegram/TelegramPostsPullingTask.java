package dj.arbuz.telegram;

import dj.arbuz.database.GroupBase;
import dj.arbuz.handlers.notifications.PostsPullingTask;
import dj.arbuz.socialnetworks.vk.AbstractVk;

import java.util.List;
import java.util.Optional;

/**
 * Класс логики потока получающего новые посты для телеграма
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class TelegramPostsPullingTask extends PostsPullingTask {
    /**
     * Поле телеграмм бота
     *
     * @see TelegramBot
     */
    private final TelegramBot telegramBot;

    protected TelegramPostsPullingTask(TelegramBot telegramBot, GroupBase groupsBase, AbstractVk vk) {
        super(groupsBase, vk);
        this.telegramBot = telegramBot;
    }

    /**
     * Метод логики выполняемой внутри {@code TelegramPostsPullingThread}
     *
     * @see GroupBase#getGroupsScreenName()
     * @see GroupBase#getSubscribedToGroupUsersId(String)
     */
    @Override
    public void run() {
        for (String groupScreenName : groupsBase.getGroupsScreenName()) {
            Optional<List<String>> threadFindNewPosts = getNewPostsAsStrings(groupScreenName);

            // проверяется наличие новых постов, могут отсутствовать по причине отсутствия новых постов или отсутствия группы в базе данных
            if (threadFindNewPosts.isPresent()) {
                for (String newPostText : threadFindNewPosts.get()) {
                    for (String userSendNewPostId : groupsBase.getSubscribedToGroupUsersId(groupScreenName)) {
                        telegramBot.send(userSendNewPostId, newPostText);
                    }
                }
            }

        }
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @throws UnsupportedOperationException - возникает тк эта операция не нужна в этой реализации класса,
     *                                       поэтому он не реализован
     */
    @Override
    public boolean hasNewPosts() {
        throw new UnsupportedOperationException(
                "Метод не реализован, тк эта реализации сразу отправляет сообщения пользователям"
        );
    }

    /**
     * Метод получающий новые посты
     *
     * @throws UnsupportedOperationException - возникает тк эта операция не нужна в этой реализации класса,
     *                                       поэтому он не реализован
     */
    @Override
    public Iterable<String> getNewPosts() {
        throw new UnsupportedOperationException(
                "Метод не реализован, тк эта реализации сразу отправляет сообщения пользователям"
        );
    }
}
