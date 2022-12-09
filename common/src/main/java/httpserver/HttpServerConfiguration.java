package httpserver;

import loaders.gson.GsonLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Класс конфигурации http сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class HttpServerConfiguration {
    /**
     * Поле ip хоста
     */
    final String hostName;
    /**
     * Поле порта, который будет использоваться
     */
    final Integer port;
    /**
     * Поле директории, доступ к которой имеют все пользователи
     */
    final String webDataPathAsString;

    /**
     * Метод создающий конфигурации из json файла, в том случае если сервер не предоставляет прямого доступа к файлам,
     * необходимо в json файле в поле {@code webDataPathAsString} явно указать, что это поле является пустой строкой
     *
     * @param pathToConfigFile путь до json файла с конфигурацией
     * @return конфигурацию прочитанную из файла
     * @throws IOException выкидывается если не были прочитаны все данные для конфигурации сервера
     */
    static HttpServerConfiguration readConfigurationFromFile(Path pathToConfigFile) throws IOException {
        GsonLoader<HttpServerConfiguration> httpServerConfigurationGsonLoader = new GsonLoader<>();
        HttpServerConfiguration loadedConfig = httpServerConfigurationGsonLoader.loadFromJson(pathToConfigFile, HttpServerConfiguration.class);
        if (loadedConfig.hostName == null || loadedConfig.port == null || loadedConfig.webDataPathAsString == null) {
            throw new IOException("Не удалось прочитать" +
                    (loadedConfig.hostName == null ? "hostName" : "") +
                    (loadedConfig.port == null ? "port" : "") +
                    (loadedConfig.webDataPathAsString == null ? "webDataPathAsString" : "")
                    );
        }
        return loadedConfig;
    }
}
