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
     * Поле кол-ва обрабатывающий сообщения потоков
     */
    public static final int AMOUNT_OF_WORKING_THREADS = 1;
    /**
     * Поле пути до файлов доступных серверу
     */
    public static final String WEB_SRC = "src/main/resources/websrc";
    /**
     * Поле файла при истечении времени аутентификации
     */
    public static final String AUTH_TIME_EXPIRED_FILE = "timeexpired.html";
    /**
     * Поле CRLF
     */
    public static final String CRLF = "\r\n";
}
