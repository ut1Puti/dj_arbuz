package handlers.notifcations;

import bots.telegram.TelegramBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import handlers.vk.groups.NoGroupException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Класс получающий новые посты для телеграмма
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class TelegramPostsPullingThread extends AbstractPostsPullingThread {
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
        while (!Thread.interrupted()) {
            Map<String, List<String>> map = storage.getBase();
            Set<String> set = map.keySet();
            try {
                for (String key : set) {
                    Optional<List<String>> optional = vk.getNewPosts(key, 0);

                    if (optional.isPresent()) {
                        for (String postInText : optional.get()) {
                            List<String> usersId = map.get(key);
                            for (String userId : usersId) {
                                SendMessage message = new SendMessage(userId, postInText);
                                telegramBot.execute(message);
                            }
                        }
                    }

                }
                final int oneMinuteInMilliseconds = 60000;
                Thread.sleep(oneMinuteInMilliseconds);
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
     * @return null, тк все посты сразу отпраляются пользователям
     */
    @Override
    public List<List<String>> getNewPosts() {
        return null;
    }
}
