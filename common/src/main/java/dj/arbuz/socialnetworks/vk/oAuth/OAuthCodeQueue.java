package dj.arbuz.socialnetworks.vk.oAuth;

import httpserver.server.HttpServer;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OAuthCodeQueue {
    /**
     * Регулярное выражение для поиска кода аутентификации с помощью oAuth 2.0 и id пользователя для которого был получен код
     */
    private static final Pattern codeAndTelegramIdRegex = Pattern.compile("code=(?<code>.+)&state=(?<id>.+)");
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final Map<String, String> communication = Collections.synchronizedMap(new WeakHashMap<>());
    private final ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);

    public OAuthCodeQueue(HttpServer httpServer) {
        service.scheduleAtFixedRate(() -> {
            if (counter.get() > 0) {
                this.addMessageFromHttpParams(httpServer.getHttpGetRequestParams());
                counter.updateAndGet(i -> Math.min(i, communication.size()));
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void subscribeWithKey(String key) {
        subscribeWithKeyHelper(key);
    }

    private static void subscribeWithKeyHelper(String key) {
        communication.compute(key, (k, v) -> v);
        counter.incrementAndGet();
    }

    public MessageQueue subscribeClosable(String key, HttpServer httpServer) {
        return new MessageQueue(key, httpServer);
    }

    public String getMessageByKey(String key) {
        return communication.get(key);
    }

    public void unsubscribeByKey(String key) {
        unsubscribeByKeyHelper(key);
    }

    private static void unsubscribeByKeyHelper(String key) {
        communication.remove(key);
        counter.updateAndGet(i -> i > 0 ? --i : i);
    }

    public void addMessageFromHttpParams(Stream<String> params) {
        addMessageFromHttpParamsHelper(params);
    }

    private static void addMessageFromHttpParamsHelper(Stream<String> params) {
        try {
            params.map(OAuthCodeQueue::getCodeAndUserTelegramIdFromHttpGetParam)
                    .filter(Objects::nonNull)
                    .forEach(OAuthCodeQueue::addMessageByKey);
        } catch (UncheckedIOException ignored) {
        }
    }

    private static void addMessageByKey(OAuthParams oAuthParams) {
        communication.put(oAuthParams.userTelegramId, oAuthParams.authCode);
    }

    /**
     * Метод, который получает code из get параметров GET запроса на сервер
     *
     * @param httpRequestGetParameters get параметры отправленные на сервер
     * @return {@code code}
     */
    private static OAuthParams getCodeAndUserTelegramIdFromHttpGetParam(String httpRequestGetParameters) {
        Matcher matcher = codeAndTelegramIdRegex.matcher(httpRequestGetParameters);
        if (matcher.find()) {
            return new OAuthParams(matcher.group("code"), matcher.group("id"));
        }
        return null;
    }

    /**
     * Хранит параметры для аутентификации с помощью oAuth 2.0
     *
     * @param authCode       код oAuth 2.0
     * @param userTelegramId id пользователя в телеграм
     */
    private record OAuthParams(String authCode, String userTelegramId) {
    }

    public static final class MessageQueue implements AutoCloseable {
        private final String key;
        private final HttpServer httpServer;

        public MessageQueue(String key, HttpServer httpServer) {
            this.key = key;
            this.httpServer = httpServer;
            subscribeWithKeyHelper(this.key);
        }

        public String pollMessage() {
            String res = null;
            while (res == null) {
                System.out.println("here");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                res = communication.get(key);
            }
            return res;
        }

        @Override
        public void close() throws Exception {
            System.out.println("here" + counter);
            unsubscribeByKeyHelper(key);
        }
    }
}
