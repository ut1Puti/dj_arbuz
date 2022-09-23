package handlers;

import java.net.URI;

public class LocalBrowserHandler {
    private static final String[] browsers = {"google-chrome", "firefox", "mozilla", "epiphany",
            "konqueror", "netscape", "opera", "links", "lynx", "chromium", "brave-browser"};

    public boolean openBrowser(URI uri) {
        try {
            if (isMacOperatingSystem()) {
                Runtime.getRuntime().exec(String.format("open %s", uri));
            } else if (isWindowsOperatingSystem()) {
                Runtime.getRuntime().exec(String.format("rundll32 url.dll,FileProtocolHandler %s", uri));
            } else {
                String browser = null;
                for (String b : browsers) {
                    if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
                        System.out.println("Attempting to open that address in the default browser now...");
                        Runtime.getRuntime().exec(new String[]{browser = b, uri.toString()});
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isMacOperatingSystem() {
        return getOperatingSystemName().startsWith("Mac OS");
    }

    private boolean isWindowsOperatingSystem() {
        return getOperatingSystemName().startsWith("Windows");
    }

    private String getOperatingSystemName() {
        return System.getProperty("os.name");
    }
}
