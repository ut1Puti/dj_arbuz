package dj.arbuz.telegram;

import com.google.gson.JsonSyntaxException;
import loaders.gson.GsonLoader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Класс хранящий конфигурацию телеграм бота
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class TelegramBotConfiguration {
    /**
     * Поле имени бота
     */
    final String botUserName;
    /**
     * Поле токена доступа бота, к telegram api
     */
    final String telegramBotToken;
    /**
     * Поле id создателя
     */
    final Long creatorId;

    /**
     * Метод загружающий объект класса из json файла
     *
     * @param telegramBotConfigurationJsonFilePath путь до json файла с конфигурацией бота
     * @return {@code TelegramBotConfiguration} с конфигурацией указанной в файле
     */
    static TelegramBotConfiguration loadTelegramBotConfigurationFromJson(Path telegramBotConfigurationJsonFilePath) {
        try {
            GsonLoader<TelegramBotConfiguration> telegramBotConfigurationGsonLoader = new GsonLoader<>();
            TelegramBotConfiguration telegramBotConfiguration =
                    telegramBotConfigurationGsonLoader.loadFromJson(telegramBotConfigurationJsonFilePath, TelegramBotConfiguration.class);

            if (telegramBotConfiguration.botUserName == null || telegramBotConfiguration.telegramBotToken == null) {
                throw new RuntimeException("В переданном файл отсутствуют необходимые для запуска данные");
            }

            return telegramBotConfiguration;
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод создающий объект класса из параметров окружения программы
     *
     * @return {@code TelegramBotConfiguration} с параметрами конфигрурации из локального окружения
     */
    static TelegramBotConfiguration loadFromEnvVariables() {
        String telegramBotName = System.getenv("telegram_bot_name");
        String telegramBotToken = System.getenv("telegram_bot_token");
        Long creatorId = Long.parseLong(System.getenv("creator_id"));

        if (telegramBotName == null) {
            throw new RuntimeException("Имя бота не может быть null");
        } else if (telegramBotToken == null) {
            throw new RuntimeException("Token бота не может быть null");
        }

        return new TelegramBotConfiguration(telegramBotName, telegramBotToken, creatorId);
    }
}
