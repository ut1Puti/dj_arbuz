package handlers.vkapi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VkAppConfigurationTests {
    private VkAppConfiguration appConfiguration;

    @Test
    public void CorrectTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/vkconfigcorrect.properties");
        assertEquals(appConfiguration.AUTH_URL, "AUTH_URL");
        assertEquals(appConfiguration.APP_ID, 2000);
        assertEquals(appConfiguration.CLIENT_SECRET, "CLIENT_SECRET");
        assertEquals(appConfiguration.REDIRECT_URL, "REDIRECT_URI");
    }

    @Test
    public void IncorrectPathTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/unknownfile.properties");
        assertNull(appConfiguration.AUTH_URL);
        assertNull(appConfiguration.APP_ID);
        assertNull(appConfiguration.CLIENT_SECRET);
        assertNull(appConfiguration.REDIRECT_URL);
    }

    @Test
    public void IncorrectAppIdTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/vkconfigincorrectappidname.properties");
        assertEquals(appConfiguration.AUTH_URL, "AUTH_URL");
        assertNull(appConfiguration.APP_ID);
        assertEquals(appConfiguration.CLIENT_SECRET, "CLIENT_SECRET");
        assertEquals(appConfiguration.REDIRECT_URL, "REDIRECT_URI");
    }

    @Test
    public void IncorrectStringsTest(){
        appConfiguration = new VkAppConfiguration("src/test/resources/testanonsrc/vkconfigincorrectstringsnames.properties");
        assertNull(appConfiguration.AUTH_URL);
        assertEquals(appConfiguration.APP_ID, 100);
        assertNull(appConfiguration.CLIENT_SECRET);
        assertNull(appConfiguration.REDIRECT_URL);
    }
}
