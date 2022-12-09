package dj.arbuz.socialnetworks.vk.oAuth;

import httpserver.HttpServer;
import lombok.EqualsAndHashCode;

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

/**
 * Класс очереди сообщений сервера
 *
 * @author Кедровских Олег
 * @version 1.0
 */
public class OAuthCodeQueue {
    /**
     * Регулярное выражение для поиска кода аутентификации с помощью oAuth 2.0 и id пользователя для которого был получен код
     */
    private static final Pattern codeAndTelegramIdRegex = Pattern.compile("code=(?<code>.+)&state=(?<id>.+)");
    /**
     * Поле счетчика текущего кол-ва подписчиков
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
    /**
     * Поле хранилища подписчиков и полученных для них ключей
     */
    private static final Map<MessageQueuePuller, String> map = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Конструктор - создает экземпляр класса
     *
     * @param httpServer http сервер, который используется для получения сообщений
     */
    public OAuthCodeQueue(HttpServer httpServer) {
        service.scheduleAtFixedRate(() -> {
            if (counter.get() > 0) {
                this.addMessageFromHttpParams(httpServer.getHttpGetRequestParams());
                counter.updateAndGet(i -> Math.min(i, map.size()));
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Метод подписывающий на уведомления по ключу
     *
     * @param key ключ
     * @return класс для получения уведомлений
     */
    public MessageQueuePuller subscribe(String key) {
        return new MessageQueuePuller(key);
    }

    /**
     * Метод подписывающий класс для получения уведомлений
     *
     * @param messageQueuePuller экземпляр класса получения уведомлений
     */
    private static void subscribePuller(MessageQueuePuller messageQueuePuller) {
        map.put(messageQueuePuller, null);
        counter.incrementAndGet();
    }

    /**
     * Метод отписывающий класс для получения уведомлений
     *
     * @param messageQueuePuller экземпляр класса получения уведомлений
     */
    private static void unsubscribePuller(MessageQueuePuller messageQueuePuller) {
        if (map.remove(messageQueuePuller) != null) {
            counter.updateAndGet(i -> i > 0 ? --i : i);
        }
    }

    private void addMessageFromHttpParams(Stream<String> params) {
        addMessageFromHttpParamsHelper(params);
    }

    /**
     * Метод добавляющий сообщения с сервера
     *
     * @param params параметры, полученные с сервера в виде {@code Stream}
     */
    private static void addMessageFromHttpParamsHelper(Stream<String> params) {
        try {
            params.map(OAuthCodeQueue::getCodeAndUserTelegramIdFromHttpGetParam)
                    .filter(Objects::nonNull)
                    .forEach(OAuthCodeQueue::addMessageByKey);
        } catch (UncheckedIOException ignored) {
        }
    }

    private static void addMessageByKey(OAuthParams oAuthParams) {
        map.put(new MessageQueuePuller(oAuthParams.userTelegramId), oAuthParams.authCode);
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

    /**
     * Класс получения уведомлений подписчиком уведомлений
     *
     * @author Кедровских Олег
     * @version 1.0
     */
    @EqualsAndHashCode
    public static final class MessageQueuePuller implements AutoCloseable {
        /**
         * Поле ключа подписки
         */
        private final String key;

        /**
         * Конструктор - создает экземпляр класса
         *
         * @param key ключ, по которому подписывается подписчик
         */
        public MessageQueuePuller(String key) {
            this.key = key;
            subscribePuller(this);
        }

        /**
         * Метод получающий ключ в течение 1 минуты
         *
         * @return строку с ответом сервера, если не удалось найти сообщение,
         * возвращается {@code null}
         */
        public String pollMessage() {
            String res = null;
            for (int i = 0; i < 36; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                res = map.get(this);
            }
            return res;
        }

        /**
         * Метод закрывающий класс получения уведомлений
         */
        @Override
        public void close() {
            unsubscribePuller(this);
        }
    }
}
