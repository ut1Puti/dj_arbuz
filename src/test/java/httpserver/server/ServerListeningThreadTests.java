package httpserver.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирующий класс обрабатывающий сообщения для сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class ServerListeningThreadTests {
    /**
     * Поле потока обрабатывающего сообщения
     */
    private ServerListenerThread serverListenerThread;

    /**
     * Метод создающий поток, перед каждым тестом
     *
     * @throws IOException - возникает при ошибке открытия сокета
     */
    @BeforeEach
    public void setListeningThread() throws IOException {
        ServerSocket serverSocket = new ServerSocket(HttpServerConfiguration.PORT);
        serverListenerThread = new ServerListenerThread(serverSocket);
        serverListenerThread.start();
    }

    /**
     * Метод останавливающий сервер после каждого теста
     */
    @AfterEach
    public void stopListeningThread() {
        serverListenerThread.stopWithInterrupt();
    }

    /**
     * Метод тестирующий метод, который проверяет, работает ли поток
     */
    @Test
    public void testIsWorking() {
        assertTrue(serverListenerThread.isWorking());
    }

    /**
     * Метод тестирующий метод, останавливающий поток
     */
    @Test
    public void testStopWithInterrupt() {
        assertTrue(serverListenerThread.isWorking());
        serverListenerThread.stopWithInterrupt();
        assertFalse(serverListenerThread.isWorking());
    }

    /**
     * Метод тестирующий метод, получающий параметры get запроса отправленного на сервер
     */
    @Test
    public void testGetHttpRequestParameters() {
        String expectedHttpParameters = "code";
        ClientBuilder.newClient().target("http://localhost:8080/redirect.html?{parameters}")
                .resolveTemplate("parameters", expectedHttpParameters).request().async().get();
        String httpParameters = serverListenerThread.getHttpRequestParameters();
        assertEquals(expectedHttpParameters, httpParameters);
    }
}
