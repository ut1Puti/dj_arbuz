package dj.arbuz.socialnetworks.vk;

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
        super(new VkAuthMock(),
                new VkGroupsMock("src/test/resources/vk_tests/groups.json"),
                new VkWallMock("src/test/resources/vk_tests/posts.json"));
    }
}