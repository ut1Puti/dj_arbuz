package handlers;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import user.User;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import static com.vk.api.sdk.objects.groups.Fields.VERIFIED;

public class HandlerVkApi {
    private final static TransportClient transportClient = new HttpTransportClient();
    private final static VkApiClient vk = new VkApiClient(transportClient);

    public static User initUser(Scanner input) {
        //do auto pulling redirect link
        WebTarget t = ClientBuilder.newClient().target("https://oauth.vk.com/authorize?client_id=51433509&display=page&redirect_uri={redirect_uri}&scope=270336&response_type={type}&v=5.131");
        t = t.resolveTemplate("redirect_uri", "https://oauth.vk.com/blank.html").resolveTemplate("type", "token");
        //t = t.resolveTemplate("redirect_uri", "https://t.me/oop_urfu_bot").resolveTemplate("type", "token");
        // t = t.resolveTemplate("redirect_uri", "http://localhost:8080/redirect.html").resolveTemplate("type", "token");
        LocalBrowserHandler b = new LocalBrowserHandler();
        b.openBrowser(t.getUri());
        //System.out.println(t.request().get());
        //String l = "http://localhost:8080/redirect.html";
        //try {
        //    b.openBrowser(new URI(l));
        //    URL q = getFinalURL(new URL(l));
        //    System.out.println(q);
        //} catch (MalformedURLException | URISyntaxException e) {
        //    System.out.println(e.getMessage());
        //}
        // end auto pulling redirect link
        //просто копируешь всю ссылку из браузера
        String link = input.nextLine();
        int id = getIdFromURL(link);
        String token = getAccessTokenFromURL(link);
        return new User(id, token);
    }

    public static URL getFinalURL(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("GET");
            con.connect();
            con.getInputStream();
            int resCode = con.getResponseCode();
            while(!(resCode == HttpURLConnection.HTTP_SEE_OTHER
                    || resCode == HttpURLConnection.HTTP_MOVED_PERM
                    || resCode == HttpURLConnection.HTTP_MOVED_TEMP)){
                System.out.println(con.getHeaderField("Location"));
            }
            if (resCode == HttpURLConnection.HTTP_SEE_OTHER
                    || resCode == HttpURLConnection.HTTP_MOVED_PERM
                    || resCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String Location = con.getHeaderField("Location");
                if (Location.startsWith("/")) {
                    Location = url.getProtocol() + "://" + url.getHost() + Location;
                }
                return getFinalURL(new URL(Location));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return url;
    }

    public static List<Group> searchGroups(String groupName, User user) {
        try {
            return vk.groups().search(user, groupName)
                    .offset(0).count(3)
                    .execute()
                    .getItems();
        } catch (ApiException | ClientException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Group searchVerifiedGroup(String groupName, User user){
        List<Group> foundGroups = searchGroups(groupName, user);
        if (foundGroups == null) {
            return null;
        }
        for (Group foundGroup : foundGroups) {
            try {
                List<GetByIdObjectLegacyResponse> foundVerifiedGroups = vk.groups().getByIdObjectLegacy(user)
                        .groupId(String.valueOf(foundGroup.getId()))
                        .fields(VERIFIED)
                        .execute();
                if (foundVerifiedGroups.size() == 1)
                    return foundVerifiedGroups.get(0);
            } catch (ApiException | ClientException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    public static boolean joinGroup(String groupName, User user) {
        Group verifiedGroup = searchVerifiedGroup(groupName, user);
        if (verifiedGroup == null) {
            return false;
        }
        if (verifiedGroup.isMember()) {
            return true;
        }
        try {
            vk.groups().join(user).groupId(verifiedGroup.getId()).execute();
            return true;
        } catch (ClientException | ApiException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void turnNotifications(boolean turn, Group group, User user) {
        try {
            vk.wall().get(user).domain(group.getScreenName()).offset(0).count(3).execute();
        } catch (ClientException | ApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int getIdFromURL(String url){
        int startIndexOfId = url.lastIndexOf("user_id=") + "user_id=".length();
        StringBuilder id = new StringBuilder("");
        while (url.charAt(startIndexOfId) != '&' && startIndexOfId < url.length() - 1) {
            id.append(url.charAt(startIndexOfId++));
        }
        return Integer.parseInt(id.toString());
    }

    private static String getAccessTokenFromURL(String url){
        int startIndexOfAccessToken = url.lastIndexOf("access_token=") + "access_token=".length();
        StringBuilder accessToken = new StringBuilder("");
        while (url.charAt(startIndexOfAccessToken) != '&' && startIndexOfAccessToken < url.length() - 1){
            accessToken.append(url.charAt(startIndexOfAccessToken++));
        }
        return accessToken.toString();
    }
}
