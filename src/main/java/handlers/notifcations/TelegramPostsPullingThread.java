package handlers.notifcations;

import bots.telegram.TelegramBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import database.GroupsStorage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

/**
 * Класс получающий новые посты для телеграма
 *
 * @author Кедровских Олег
 * @version 1.0
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
     * @param telegramBot - телеграмм бот
     */
    public TelegramPostsPullingThread(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Метод логики выполняемой внутри потока
     *
     * @see GroupsStorage#getGroups()
     * @see handlers.vk.Vk#getNewPosts(GroupsStorage, String)
     * @see GroupsStorage#getSubscribedToGroupUsersId(String)
     */
    @Override
    public void run() {
        while (working) {
            try {
                for (String groupScreenName : groupsBase.getGroups()) {
                    Optional<List<String>> threadFindNewPosts = vk.getNewPosts(groupsBase, groupScreenName);

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
            } catch (ApiException | ClientException ignored) {
            }
        }
        working = false;
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
