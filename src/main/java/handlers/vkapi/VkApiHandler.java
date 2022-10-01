package handlers.vkapi;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ApiTokenExtensionRequiredException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import database.Storage;
import httpserver.HttpServer;
import user.CreateUser;
import user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vk.api.sdk.objects.groups.Fields;

/**
 * Класс обрабатывающий запросы пользователя к Vk API
 *
 * @author Кедровских Олег
 * @author Щеголев Андрей
 * @version 0.6
 */
public class VkApiHandler implements CreateUser {
    /** Поле транспортного клиента */
    private final TransportClient transportClient = new HttpTransportClient();
    /** Поле класс позволяющего работать с Vk SDK Java */
    private final VkApiClient vk = new VkApiClient(transportClient);
    /** Поле сервера получающего токены пользователя и переправляющего пользователей на tg бота */
    private HttpServer httpServer = null;
    private Storage dataBase = null;
    /** Поле конфигурации vk приложения */
    private final VkAppConfiguration appConfiguration;

    /**
     * Конструктор по пути до файла с конфигурацией приложения
     * @param configPath - путь до файла с конфигурацией
     */
    public VkApiHandler(String configPath) {
        appConfiguration = new VkAppConfiguration(configPath);
    }

    /**
     * Метод возвращающий ссылку для аутентификации
     * @return ссылку для аутентификации, если сервер недоступен, то это null
     */
    public String getAuthURL() {
        try {
            httpServer = HttpServer.getInstance();
        } catch (IOException e){
            return null;
        }
        return appConfiguration.AUTH_URL;
    }

    /**
     * Метод интерфейса CreateUser создающий пользователя.
     * Создается с помощью Vk Java SDK, получая код с сервера
     * @return интерфейс для создания пользователя
     */
    @Override
    public User createUser() {
        try {
            httpServer = HttpServer.getInstance();
            String authCode = getAuthCodeFromHttpRequest(httpServer.getHttpRequestGetParametrs());
            UserAuthResponse authResponse = vk.oAuth()
                    .userAuthorizationCodeFlow(
                            appConfiguration.APP_ID,
                            appConfiguration.CLIENT_SECRET,
                            appConfiguration.REDIRECT_URL,
                            authCode)
                    .execute();
            return new User(authResponse.getUserId(), authResponse.getAccessToken());
        } catch (ApiException | IOException | ClientException e) {
            return null;
        }
    }

    /**
     * Метод, который ищет все группы по запросу
     * @param groupName - запрос
     * @param callingUser - пользователь сделавщий запрос
     * @return список групп полученных по запросу
     * @throws ApiTokenExtensionRequiredException - возникает если токен пользователя истек
     */
    public List<Group> searchGroups(String groupName, User callingUser) throws ApiTokenExtensionRequiredException {
        try {
            return vk.groups().search(callingUser, groupName)
                    .offset(0).count(5)
                    .execute()
                    .getItems();
        } catch (ApiException | ClientException e){
            return null;
        }
    }

    /**
     * Метод, который ищет подтвержденные группы по запросу
     * @param groupName - запрос
     * @param callingUser - пользователь сделавщий запрос
     * @return верифицированную группу
     *         если групп оказалось больше одной возвращает с большим числом подписчиков
     *         если верифицированная группа не нашлась, возвращает null
     * @throws ApiTokenExtensionRequiredException - возникает если токен пользователя истек
     */
    public Group searchGroup(String groupName, User callingUser) throws ApiTokenExtensionRequiredException {
        List<Group> foundGroups = searchGroups(groupName, callingUser);

        if (foundGroups == null || foundGroups.size() == 0) {
            return null;
        }

        int maxMembersCount = Integer.MIN_VALUE;
        Group resultGroup = null;
        for (Group foundGroup : foundGroups) {
            List<GetByIdObjectLegacyResponse> foundByIdGroups;
            try {
                foundByIdGroups = vk.groups()
                        .getByIdObjectLegacy(callingUser)
                        .groupId(String.valueOf(foundGroup.getId()))
                        .fields(Fields.MEMBERS_COUNT)
                        .execute();
            } catch (ApiException | ClientException e) {
                continue;
            }

            if (foundByIdGroups.isEmpty()) {
                continue;
            }

            GetByIdObjectLegacyResponse foundByIdGroup = foundByIdGroups.get(0);
            String[] foundByIdGroupNames = foundByIdGroup.getName().split("[/|]");
            for (String foundByIdGroupName : foundByIdGroupNames) {

                if (isNameDifferent(groupName, foundByIdGroupName)) {
                    continue;
                }

                if (foundByIdGroup.getMembersCount() > maxMembersCount) {
                    maxMembersCount = foundByIdGroup.getMembersCount();
                    resultGroup = foundByIdGroup;
                }

            }
        }
        return resultGroup;
    }


    public void subscribeTo(String groupName, User callingUser) throws ApiTokenExtensionRequiredException {
        Group resultBeforeSearch = searchGroup(groupName, callingUser);
        if(dataBase == null) {
            dataBase = Storage.storageGetInstance();
        }
        dataBase.addInfoToGroup(resultBeforeSearch.getScreenName(),String.valueOf(callingUser.getId()));
    }

    /**
     *
     * @param turn - показывает надо ли включить или выключить уведомления
     * @param groupName - запрос
     * @param callingUser - пользователь сделваший запрос
     * @throws ApiTokenExtensionRequiredException - возникает если токен пользователя истек
     */
    public void turnNotifications(boolean turn, String groupName, User callingUser) throws ApiTokenExtensionRequiredException {
        Group foundGroup = searchGroup(groupName, callingUser);
        try {
            vk.wall().get(callingUser).domain(foundGroup.getScreenName()).offset(0).count(3).execute();
        } catch (ClientException | ApiException e) {
            return;
        }
    }

    /**
     * Метод, который получает code из get параметров GET запроса на сервер
     * @param httpRequestGetParameters - get параметры отправленные на сервер
     * @return code
     */
    private String getAuthCodeFromHttpRequest(String httpRequestGetParameters) {
        StringBuilder authCodeBuilder = new StringBuilder();
        for (int i = httpRequestGetParameters.lastIndexOf("code=") + "code=".length(); i < httpRequestGetParameters.length(); i++){

            if (httpRequestGetParameters.charAt(i) == '&'){
                break;
            }

            authCodeBuilder.append(httpRequestGetParameters.charAt(i));
        }
        return authCodeBuilder.toString();
    }

    /**
     * Метод проверяет есть ли разница между двумя строками
     * @param baseName - изначальное имя
     * @param searchName - имя поиска
     * @return true - если разница хотя бы в одном слове больше 50%
     *         false - если разница в обоих словах меньше 50%
     */
    private boolean isNameDifferent(String baseName, String searchName) {
        String lowerCaseBaseName = baseName.toLowerCase();
        String lowerCaseSearchName = searchName.toLowerCase();

        Pair<String> diffPair = stringDifference(lowerCaseBaseName, lowerCaseSearchName);

        int baseNameDiff = (int)((double) diffPair.first.length() / (double) baseName.length() * 100);
        int searchNameDiff = (int)((double) diffPair.second.length() / (double) searchName.length() * 100);

        return baseNameDiff > 50 || searchNameDiff > 50;
    }

    /**
     * Метод, который ищет буквы в которых отличаются две строки
     * @param firstString - первая строка
     * @param secondString - вторая строка
     * @return пару, элементы которой это строки состоящие из букв в которых слова различаются
     */
    private Pair<String> stringDifference(String firstString, String secondString) {
        return diffHelper(firstString, secondString, new HashMap<>());
    }

    /**
     * Метод рекурсивно ищущий несовпадающие элементы
     * @param firstString - первая строка
     * @param secondString - вторая строка
     * @param lookup - Map хранящий не совпавшие элементы
     * @return пару строк состоящих из не совпавших символов
     */
    private Pair<String> diffHelper(String firstString, String secondString, Map<Long, Pair<String>> lookup) {
        long key = ((long) firstString.length()) | secondString.length();
        if (!lookup.containsKey(key)) {
            Pair<String> value;

            if (firstString.isEmpty() || secondString.isEmpty()) {
                value = new Pair<>(firstString, secondString);
            } else if (firstString.charAt(0) == secondString.charAt(0)) {
                value = diffHelper(firstString.substring(1), secondString.substring(1), lookup);
            } else {
                Pair<String> firstStringDifferences = diffHelper(firstString.substring(1), secondString, lookup);
                Pair<String> secondStringDifferences = diffHelper(firstString, secondString.substring(1), lookup);

                if (firstStringDifferences.first.length() + firstStringDifferences.second.length() < secondStringDifferences.first.length() + secondStringDifferences.second.length()) {
                    value = new Pair<>(firstString.charAt(0) + firstStringDifferences.first, firstStringDifferences.second);
                } else {
                    value = new Pair<>(secondStringDifferences.first, secondString.charAt(0) + secondStringDifferences.second);
                }

            }

            lookup.put(key, value);
        }
        return lookup.get(key);
    }

    /**
     * Хранит элементы пары
     * @param first - первый элемент
     * @param second - второй элемент
     * @param <T> - тип хранимых элементов
     */
    private record Pair<T>(T first, T second){};
}
