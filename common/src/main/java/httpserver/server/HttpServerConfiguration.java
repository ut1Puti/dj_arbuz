package httpserver.server;

//сделать загрузку данных из файла

/**
 * Класс содержащий файлы конфигурации сервера
 *
 * @author Кедровских Олег
 * @version 0.1
 */
public class HttpServerConfiguration {
    /**
     * Поле с номером порта к которому подключается сервер
     */
    public static final int PORT = 8080;
    /**
     * Поле пути до файлов доступных серверу
     */
    public static final String WEB_SRC = "common/src/main/resources/websrc";
    /**
     * Поле CRLF
     */
    public static final String CRLF = "\r\n";
}
