package dj.arbuz.socialnetworks.vk;
<<<<<<<< HEAD:common/src/test/java/dj/arbuz/socialnetworks/vk/VkMock.java

import java.nio.file.Path;
========
>>>>>>>> developTaskFour:src/test/java/dj/arbuz/socialnetworks/vk/VkMock.java

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
                new VkGroupsMock(Path.of("src/test/resources/vk_tests/groups.json")),
                new VkWallMock(Path.of("src/test/resources/vk_tests/posts.json"))
        );
    }
}