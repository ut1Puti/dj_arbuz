package handlers.notifcations;

import bots.telegram.TelegramBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.vk.groups.NoGroupException;
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
     */
    @Override
    public void run() {
        working = true;
        while (working && !isInterrupted()) {
            try {
                for (String groupScreenName : groupsBase.getGroups()) {
                    Optional<List<String>> threadFindNewPosts = vk.getNewPosts(groupScreenName, 0);

                    if (threadFindNewPosts.isPresent()) {
                        for (String postsAttachments : threadFindNewPosts.get()) {
                            for (String userId : groupsBase.getSubscribedToGroupUsersId(groupScreenName)) {
                                SendMessage message = new SendMessage(userId, postsAttachments);
                                try {
                                    telegramBot.execute(message);
                                } catch (TelegramApiException ignored){}
                            }
                        }
                    }

                }
                final int oneHourInMilliseconds = 3600000;
                sleep(oneHourInMilliseconds);
            } catch (InterruptedException e) {
                break;
            } catch (NoGroupException ignored) {
            } catch (ApiException | ClientException e) {
                throw new RuntimeException(e);
            }
        }
        working = false;
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
