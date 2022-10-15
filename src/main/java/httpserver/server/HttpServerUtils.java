package httpserver.server;

import java.io.Closeable;
import java.io.IOException;

/**
 * Класс утилитных методов http сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class HttpServerUtils {
    /**
     * Закрывает любой поток(не тот что исполнения) сервера
     *
     * @param serverStream - поток(не тот что исполнения) сервера
     */
    static void closeServerStream(Closeable serverStream) {
        try {

            if (serverStream != null) {
                serverStream.close();
            }

        } catch (IOException ignored) {}
    }
}
