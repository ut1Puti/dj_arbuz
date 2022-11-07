package socialnetworks.vk;

/**
 * Класс тестовой реализации vk
 *
 * @author Кедровских Олег
 * @version 1.0
 */
class VkMock extends AbstractVk {
    /**
     * Конструктор - создает экземпляр класса
     */
    VkMock() {
        super(new VkAuthMock(), new VkGroupsMock(), new VkWallMock());
    }
}