package dj.arbuz.handlers.notifications;

import dj.arbuz.database.GroupBase;
import dj.arbuz.socialnetworks.vk.VkMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class PostsPullingTaskTests {
    private final GroupBase groupBase = Mockito.mock(GroupBase.class);
    private static final VkMock vk = new VkMock();
    private final PostsPullingTask postsPullingThread = new PostsPullingTask(groupBase, vk) {
        @Override
        public void run() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNewPosts() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getNewPosts() {
            throw new UnsupportedOperationException();
        }
    };

    @Test
    public void testGettingNewPostsFromExistGroupWithSmallDate() {
        Mockito.when(groupBase.getGroupLastPostDate("sqwore"))
                .thenReturn(Optional.of(100L));
        Assertions.assertNotEquals(Optional.empty(), postsPullingThread.getNewPostsAsStrings("sqwore"));
    }

    @Test
    public void testGettingNewPostsIfGroupDoesNotExist() {
        Mockito.when(groupBase.getGroupLastPostDate("group2"))
                .thenReturn(Optional.empty());
        Assertions.assertEquals(Optional.empty(), postsPullingThread.getNewPostsAsStrings("group2"));
    }

    @Test
    public void testGettingPostsFromExistGroupWithBigDate() {
        Mockito.when(groupBase.getGroupLastPostDate("sqwore"))
                .thenReturn(Optional.of(Long.MAX_VALUE));
        Assertions.assertEquals(Optional.empty(), postsPullingThread.getNewPostsAsStrings("sqwore"));
    }
}
