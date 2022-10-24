package handlers.notifcations;

import bots.telegram.TelegramBot;
import database.GroupsStorage;
import socialnetworks.socialnetwork.SocialNetworkException;
import socialnetworks.socialnetwork.SocialNetwork;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

/**
 * Класс получающий новые посты для телеграма
 *
 * @author Кедровских Олег
 * @version 1.3
 * @see PostsPullingThread
 */
public class TelegramPostsPullingThread extends PostsPullingThread {
    /**
     * Поле телеграмм бота
     *
     * @see TelegramBot
     */
    private final TelegramBot telegramBot;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot телеграмм бот
     * @param groupsStorage база данных групп на которые оформлена подписка
     * @param socialNetwork социальная сети реализующая необходимые для работы методы
     */
    public TelegramPostsPullingThread(TelegramBot telegramBot, GroupsStorage groupsStorage, SocialNetwork socialNetwork) {
        super(groupsStorage, socialNetwork);
        this.telegramBot = telegramBot;
    }

    /**
     * Метод логики выполняемой внутри {@code TelegramPostsPullingThread}
     *
     * @see GroupsStorage#getGroups()
     * @see SocialNetwork#getNewPosts(GroupsStorage, String)
     * @see GroupsStorage#getSubscribedToGroupUsersId(String)
     */
    @Override
    public void run() {
        while (working.get()) {
            try {
                for (String groupScreenName : groupsBase.getGroups()) {
                    Optional<List<String>> threadFindNewPosts = socialNetwork.getNewPosts(groupsBase, groupScreenName);

                    if (threadFindNewPosts.isPresent()) {
                        for (String postsAttachments : threadFindNewPosts.get()) {
                            for (String userId : groupsBase.getSubscribedToGroupUsersId(groupScreenName)) {
                                SendMessage message = new SendMessage(userId, postsAttachments);
                                try {
                                    telegramBot.execute(message);
                                } catch (TelegramApiException ignored) {
                                }
                            }
                        }
                    }

                }
                final int oneHourInMilliseconds = 3600000;
                Thread.sleep(oneHourInMilliseconds);
            } catch (InterruptedException e) {
                break;
            } catch (SocialNetworkException ignored) {
            }
        }
        working.set(false);
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @throws UnsupportedOperationException - возникает тк эта операция не нужна в этой реализации класса,
     * поэтому он не реализован
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
     * поэтому он не реализован
     */
    @Override
    public List<String> getNewPosts() {
        throw new UnsupportedOperationException(
                "Метод не реализован, тк реализация сразу отправляет сообщения пользователям сразу"
        );
    }
}
