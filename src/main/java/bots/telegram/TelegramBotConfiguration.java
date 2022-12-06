package bots.telegram;

import com.google.gson.JsonSyntaxException;
import loaders.gson.GsonLoader;

import java.io.IOException;
import java.util.Objects;

/**
 * Класс хранящий конфигурацию телеграм бота
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
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
     * Конструктор - создает экземпляр класса
     *
     * @param botUserName      имя бота
     * @param telegramBotToken токен доступа бота к telegram api
     */
    private TelegramBotConfiguration(String botUserName, String telegramBotToken) {
        this.botUserName = botUserName;
        this.telegramBotToken = telegramBotToken;
    }

    /**
     * Метод загружающий объект класса из json файла
     *
     * @param telegramBotConfigurationJsonFilePath путь до json файла с конфигурацией бота
     * @return {@code TelegramBotConfiguration} с конфигурацией указанной в файле
     */
    static TelegramBotConfiguration loadTelegramBotConfigurationFromJson(String telegramBotConfigurationJsonFilePath) {
        try {
            GsonLoader<TelegramBotConfiguration> telegramBotConfigurationGsonLoader =
                    new GsonLoader<>(TelegramBotConfiguration.class);
            TelegramBotConfiguration telegramBotConfiguration =
                    telegramBotConfigurationGsonLoader.loadFromJson(telegramBotConfigurationJsonFilePath);

            if (telegramBotConfiguration.botUserName == null || telegramBotConfiguration.telegramBotToken == null) {
                throw new RuntimeException("В переданном файл отсутствуют необходимые для запуска данные");
            }

            return telegramBotConfiguration;
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <<<<<<< HEAD
     * Метод создающий объект класса из параметров окружения программы
     *
     * @return {@code TelegramBotConfiguration} с параметрами конфигрурации из локального окружения
     */
    static TelegramBotConfiguration loadFromEnvVariables() {
        String telegramBotName = System.getenv("telegram_bot_name");
        String telegramBotToken = System.getenv("telegram_bot_token");

        if (telegramBotName == null) {
            throw new RuntimeException("Имя бота не может быть null");
        } else if (telegramBotToken == null) {
            throw new RuntimeException("Token бота не может быть null");
        }

        return new TelegramBotConfiguration(telegramBotName, telegramBotToken);
    }

    /**
     * Метод вычисляющий хэш экземляра класса
     *
     * @return хэш экземпляра
     */
    @Override
    public int hashCode() {
        return Objects.hash(telegramBotToken, botUserName);
    }

    /**
     * Метод проверяющий равенство {@code obj} и {@code TelegramBotConfiguration}
     *
     * @param obj - сравниваемый объект
     * @return {@code true} если объекты равны по полям, {@code false} если объекты не равны по полям
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TelegramBotConfiguration anotherTelegramBotConfiguration)) {
            return false;
        }

        return Objects.equals(botUserName, anotherTelegramBotConfiguration.botUserName) &&
                Objects.equals(telegramBotToken, anotherTelegramBotConfiguration.telegramBotToken);
    }
}
