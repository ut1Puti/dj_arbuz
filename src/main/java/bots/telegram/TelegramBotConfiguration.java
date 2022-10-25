package bots.telegram;

import com.google.gson.JsonSyntaxException;
import loaders.gson.GsonLoader;
import loaders.gson.GsonLoadable;

import java.io.IOException;
import java.util.Objects;

/**
 * Класс хранящий конфигурацию телеграм бота
 *
 * @author Щёголев Андрей
 * @version 1.0
 */
public final class TelegramBotConfiguration implements GsonLoadable {
    /**
     * Поле объекта, который выполняет чтение json файла и преобразования в объект
     *
     * @see GsonLoader
     */
    private static final GsonLoader<TelegramBotConfiguration> TELEGRAM_BOT_CONFIGURATION_GSON_LOADER =
            new GsonLoader<>(TelegramBotConfiguration.class);
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
     * @param botUserName имя бота
     * @param telegramBotToken токен доступа бота к telegram api
     */
    public TelegramBotConfiguration(String botUserName, String telegramBotToken) {
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
            return TELEGRAM_BOT_CONFIGURATION_GSON_LOADER.loadFromJson(telegramBotConfigurationJsonFilePath);
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
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
     * @return true если объекты равны по полям, false если объекты не равны по полям
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
