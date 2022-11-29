package dj.arbuz.socialnetworks.vk;

/**
 * Класс констант используемых в vkapi
 *
 * @author Кедровских Олег
 * @version 2.0
 */
public final class VkConstants {
    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private VkConstants() {
        throw new IllegalStateException("Нельзя создавать этот класс");
    }

    /**
     * Поле содерщащее адрес vk
     */
    public static final String VK_ADDRESS = "https://vk.com/";
    /**
     * Поле адреса стены
     */
    public static final String VK_WALL_ADDRESS = VK_ADDRESS + "wall";
    /**
     * Поле стандартного offset'а
     */
    public static final int DEFAULT_OFFSET = 0;
    /**
     * Поле индекса первого элемента
     */
    public static final int FIRST_ELEMENT_INDEX = 0;
    /**
     * Поле стандартного размера запроса групп по ключу
     */
    public static final int DEFAULT_GROUPS_NUMBER = 5;
}
