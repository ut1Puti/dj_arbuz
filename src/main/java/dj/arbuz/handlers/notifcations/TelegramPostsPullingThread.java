package dj.arbuz.handlers.notifcations;

import dj.arbuz.bots.telegram.TelegramBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import dj.arbuz.handlers.vk.groups.NoGroupException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

/**
 * Класс получающий новые посты для телеграма
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class TelegramPostsPullingThread extends PostsPullingThread {
    /**
     * Поле телеграмм бота
     */
    private final TelegramBot telegramBot;
    private String[] usersIgnoredId;

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param telegramBot - телеграмм бот
     */
    public TelegramPostsPullingThread(TelegramBot telegramBot, String... usersIgnoredId) {
        this.telegramBot = telegramBot;
        this.usersIgnoredId = usersIgnoredId;
    }

    /**
     * Метод логики выполняемой внутри потока
     */
    @Override
    public void run() {
        working = true;
        while (working) {
            try {
                for (String groupScreenName : groupsBase.getGroups()) {
                    Optional<List<String>> threadFindNewPosts = vk.getNewPosts(groupScreenName, 0);

                    if (threadFindNewPosts.isPresent()) {
                        for (String postsAttachments : threadFindNewPosts.get()) {
                            List<String> groupFilteredSubscribersId = groupsBase
                                    .getSubscribedToGroupUserIdWithFilteredIds(groupScreenName, usersIgnoredId);
                            for (String userId : groupFilteredSubscribersId) {
                                SendMessage message = new SendMessage(userId, postsAttachments);
                                telegramBot.execute(message);
                            }
                        }
                    }

                }
                final int oneHourInMilliseconds = 3600000;
                Thread.sleep(oneHourInMilliseconds);
            } catch (NoGroupException ignored) {
            } catch (InterruptedException e) {
                break;
            } catch (ApiException | ClientException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Метод проверяющий наличие новых постов
     *
     * @return false, тк все посты сразу отправляются пользователям
     */
    @Override
    public boolean hasNewPosts() {
        return false;
    }

    /**
     * Метод получающий новые посты
     *
     * @return null, тк все посты сразу отправляются пользователям
     */
    @Override
    public List<String> getNewPosts() {
        return null;
    }
}
