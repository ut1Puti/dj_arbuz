package handlers.vkapi;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

/**
 * Класс констант используемых в vkapi
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class VkApiConsts {
    /** Поле содерщащее адрес vk */
    static final String VK_ADDRESS = "https://vk.com/";
    /** Поле стандартного offset'а */
    static final int DEFAULT_OFFSET = 0;
    /** Поле индекса первого элемента */
    static final int FIRST_ELEMENT_INDEX = 0;
    /** Поле стандартного размера запроса групп по ключу */
    static final int DEFAULT_GROUPS_NUMBER = 5;
}
