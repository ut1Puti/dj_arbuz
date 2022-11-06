package bots.telegram;

import com.google.gson.JsonSyntaxException;
import loaders.gson.GsonLoader;
import loaders.gson.GsonLoadable;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

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
    private TelegramBotConfiguration(String botUserName, String telegramBotToken) {
        this.botUserName = botUserName;
        this.telegramBotToken = telegramBotToken;
    }

    /**
     * Метод для создания json файла с конфигурацией телеграм юота
     *
     * @param args переменные запуска
     */
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Введите токен бота: ");
        String telegramBotToken = userInput.next();
        System.out.print("Введите имя бота: ");
        String telegramBotName = userInput.next();
        TelegramBotConfiguration telegramBotConfiguration = new TelegramBotConfiguration(telegramBotName, telegramBotToken);
        String telegramBotConfigurationJsonFilePath = "";
        while (!telegramBotConfigurationJsonFilePath.matches(".*\\.json")) {
            System.out.print("Введите путь, до json файла, в который будет сохранена конфигурация:");
            telegramBotConfigurationJsonFilePath = userInput.next();
        }
        try {
            TELEGRAM_BOT_CONFIGURATION_GSON_LOADER.loadToJson(telegramBotConfigurationJsonFilePath, telegramBotConfiguration);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить конфигурацию в файл");
        } finally {
            userInput.close();
        }
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
