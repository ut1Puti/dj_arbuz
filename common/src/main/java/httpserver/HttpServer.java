package httpserver;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс http сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class HttpServer extends NanoHTTPD {
    /**
     * Поле хранящее instance сервера, запущенного в текущем приложении
     */
    private static HttpServer httpServerInstance;
    /**
     * Поле конфигурации http сервера
     */
    private final HttpServerConfiguration httpServerConfiguration;
    /**
     * Поле хранящее query параметры пришедшие на сервер
     */
    private final Map<String, String> map = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Конструктор - создает instance сервера
     *
     * @param serverConfig конфигурация с которой запуститься сервер
     */
    private HttpServer(HttpServerConfiguration serverConfig) {
        super(serverConfig.hostName, serverConfig.port);
        this.httpServerConfiguration = serverConfig;
    }

    /**
     * Создает instance и возвращает его. Если экземпляр уже был создан, но не был удален,
     * будет выброшено {@link IllegalStateException}
     *
     * @param serverConfigPath путь до файла с конфигурацией сервера
     * @return instance {@code HttpServer}
     * @throws IOException возникает при ошибках чтения конфигурации или создания сервера с указанными в нем параметрами
     */
    public static HttpServer createInstance(Path serverConfigPath) throws IOException {
        if (httpServerInstance == null) {
            httpServerInstance = new HttpServer(HttpServerConfiguration.readConfigurationFromFile(serverConfigPath));
        } else {
            throw new IllegalStateException("Вы не можете создать сервер повторно");
        }
        return httpServerInstance;
    }

    /**
     * Метод возвращающий instance сервера
     *
     * @throws IllegalStateException возникает если к моменту вызова, сервер не был создан
     * @return instance сервера
     */
    public static HttpServer getInstance() {
        if (httpServerInstance == null) {
            throw new IllegalStateException("Сервер не создан");
        }
        return httpServerInstance;
    }

    /**
     * Обрабатывает http request и отправляет ответ на основе него пользователю
     *
     * @param session http сессия
     * @return http response, который будет отправлен пользователю
     */
    @Override
    public Response serve(IHTTPSession session) {
        switch (session.getMethod()) {
            case GET -> {
                Path requestedFile = Path.of(httpServerConfiguration.webDataPathAsString + session.getUri()).normalize();

                if (!requestedFile.startsWith(httpServerConfiguration.webDataPathAsString)) {
                     return newFixedLengthClosedConnectionResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "Запрошенный файл не найден");
                }

               String httpGetParams = session.getQueryParameterString();

                if (httpGetParams != null) {
                    map.put(httpGetParams, null);
                }

                try {
                    return newFixedLengthClosedConnectionResponse(Status.OK, MIME_HTML, readRequestedFile(requestedFile));
                } catch (IOException e) {
                    return newFixedLengthClosedConnectionResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal server error: " + e.getLocalizedMessage());
                }
            }
            case HEAD -> {
                return newFixedLengthClosedConnectionResponse(Status.OK, MIME_PLAINTEXT, "Ответ принят");
            }
            default -> {
                return newFixedLengthClosedConnectionResponse(Status.NOT_IMPLEMENTED, MIME_PLAINTEXT, "Этот метод не реализован");
            }
        }
    }

    /**
     * Метод создающий http response, при котором после отправки соединение с пользователем будет закрыто
     *
     * @param responseStatus статус ответа сервера
     * @param dataType тип данных отправленных сервером в body
     * @param txt данные отправленные в body
     * @return http response
     */
    private Response newFixedLengthClosedConnectionResponse(Status responseStatus, String dataType, String txt) {
        Response response = newFixedLengthResponse(responseStatus, dataType, txt);
        response.addHeader("Connection", "close");
        return response;
    }

    /**
     * Читает запрошенный пользователем файл, по предоставленному пути
     *
     * @param pathToFile путь до файла полученный от пользователя
     * @return строку содержащую данные прочитанные из запрошенного файла
     * @throws IOException возникает при ошибках чтения файла
     */
    private String readRequestedFile(Path pathToFile) throws IOException {
        try (Stream<String> fileDataStream = Files.lines(pathToFile, StandardCharsets.UTF_8)) {
            return fileDataStream.collect(Collectors.joining(" "));
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Возвращает {@code Stream<String>} с query параметрами хранящимися на сервере
     *
     * @return {@code Stream<String>} с query параметрами
     */
    public Stream<String> getHttpGetRequestParams() {
        return map.keySet().stream();
    }
}
