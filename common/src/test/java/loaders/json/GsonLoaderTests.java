package loaders.json;

import loaders.gson.GsonLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class GsonLoaderTests {
    private final GsonLoader<TestSavingClass> testGsonLoader = new GsonLoader<>();
    private static final Path[] createdDuringTestPaths = new Path[2];
    private static final Path existFile = Path.of("src", "test", "resources", "gson_test_2.json");
    private static final Path[] loadingTestPaths = new Path[7];

    {
        createdDuringTestPaths[0] = Path.of("src", "test", "resources", "gson_test_1.json");
        createdDuringTestPaths[1] = Path.of("src", "test", "resources", "gson_test_6.json");
        loadingTestPaths[0] = Path.of("src", "test", "resources", "gson_test_4.json");
        loadingTestPaths[1] = Path.of("src", "test", "resources", "not_exist.json");
        loadingTestPaths[2] = Path.of("src", "test", "resources", "gson_test_5.json");
        loadingTestPaths[3] = Path.of("src", "test", "resources", "gson_test_7.json");
        loadingTestPaths[4] = Path.of("src", "test", "resources", "gson_test_8.json");
        loadingTestPaths[5] = Path.of("src", "test", "resources", "gson_test_9.json");
        loadingTestPaths[6] = Path.of("src", "test", "resources", "gson_test_10.txt");
    }

    @BeforeAll
    public static void setUp() throws IOException {
        Files.write(existFile, "{\"some data\":  \"data\"}".getBytes());
    }

    @Test
    public void testSavingObjectToNotExistingFile() throws IOException {
        TestSavingClass testSavingClass = new TestSavingClass(new int[]{1, 2, 3}, "hello");
        testGsonLoader.loadToJson(createdDuringTestPaths[0], testSavingClass);
        Assertions.assertTrue(Files.exists(createdDuringTestPaths[0]));
    }

    @Test
    public void testSavingNullValues() throws IOException {
        TestSavingClass testSavingClass = new TestSavingClass(null, null);
        testGsonLoader.loadToJson(createdDuringTestPaths[1], testSavingClass);
        Assertions.assertTrue(Files.exists(createdDuringTestPaths[1]));
    }

    @Test
    public void testSavingToExistFile() throws IOException {
        Assertions.assertTrue(Files.exists(existFile));
        TestSavingClass testSavingClass = new TestSavingClass(new int[]{1, 2, 3}, "hello");
        testGsonLoader.loadToJson(existFile, testSavingClass);
        Assertions.assertArrayEquals(Files.readAllBytes(existFile), testGsonLoader.toJsonString(testSavingClass).getBytes());
    }

    @Test
    public void testSavingNullObject() throws IOException {
        Path notCreatedPath = Path.of("src", "test", "resources", "gson_test_3.json");
        TestSavingClass testSavingClass = null;
        testGsonLoader.loadToJson(notCreatedPath, testSavingClass);
        Assertions.assertFalse(Files.exists(notCreatedPath));
    }

    @Test
    public void testLoadFromExistFile() throws IOException {
        TestSavingClass expectedInstance = new TestSavingClass(new int[]{90, 100, 101}, "world");
        TestSavingClass actualInstance = testGsonLoader.loadFromJson(loadingTestPaths[0], TestSavingClass.class);
        Assertions.assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void testLoadFromNotExistFile() {
        Assertions.assertThrows(IOException.class, () -> testGsonLoader.loadFromJson(loadingTestPaths[1], TestSavingClass.class));
    }

    @Test
    public void testLoadFromEmptyFile() throws IOException {
        TestSavingClass actualInstance = testGsonLoader.loadFromJson(loadingTestPaths[2], TestSavingClass.class);
        Assertions.assertNull(actualInstance);
    }

    @Test
    public void testReadingNullValues() throws IOException {
        TestSavingClass expectedInstance = new TestSavingClass(null, null);
        TestSavingClass actualInstance = testGsonLoader.loadFromJson(loadingTestPaths[3], TestSavingClass.class);
        Assertions.assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void testOneNullValueRead() throws IOException {
        TestSavingClass expectedInstance = new TestSavingClass(null, "world");
        TestSavingClass actualInstance = testGsonLoader.loadFromJson(loadingTestPaths[4], TestSavingClass.class);
        Assertions.assertEquals(expectedInstance, actualInstance);
    }

    @Test
    public void testIncorrectFieldName() throws IOException {
        TestSavingClass expectedInstance = new TestSavingClass(new int[]{1, 2, 3}, "world");
        TestSavingClass actualInstance = testGsonLoader.loadFromJson(loadingTestPaths[5], TestSavingClass.class);
        Assertions.assertNotEquals(expectedInstance, actualInstance);
    }

    @Test
    public void testReadingNotJsonFileWithJsonStruct() throws IOException {
        TestSavingClass expectedInstance = new TestSavingClass(new int[]{1, 2, 3}, "world");
        TestSavingClass actualInstance = testGsonLoader.loadFromJson(loadingTestPaths[6], TestSavingClass.class);
        Assertions.assertNotEquals(expectedInstance, actualInstance);
    }

    @AfterAll
    public static void deleteCreatedFiles() {
        for (Path deletePath : createdDuringTestPaths) {
            try {
                Files.deleteIfExists(deletePath);
            } catch (IOException ignored) {
            }
        }
    }

    private static class TestSavingClass {
        private TestSavingClass(int[] array, String savedString) {
            this.array = array;
            this.savedString = savedString;
        }

        int[] array;
        String savedString;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestSavingClass testSavingClass)) {
                return false;
            }

            return Arrays.equals(array, testSavingClass.array) && Objects.equals(savedString, testSavingClass.savedString);
        }
    }
}
