package handlers;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.groups.responses.GetByIdObjectLegacyResponse;
import httpserver.server.HttpServer;
import user.User;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.vk.api.sdk.objects.groups.Fields.VERIFIED;

public class HandlerVkApi {
    private final static TransportClient transportClient = new HttpTransportClient();
    private final static VkApiClient vk = new VkApiClient(transportClient);

    public User initUser(Scanner input) {
        //do auto pulling code
        HttpServer server;
        try {
            server = HttpServer.getInstance();
            server.start();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        String url = "https://oauth.vk.com/authorize?client_id=51434490&display=page&redirect_uri=http://localhost:8080/redirect.html&scope=270336&response_type=code&v=5.131";
        WebTarget t = ClientBuilder.newClient().target(url);
        System.out.println(t.request().get());
        // end auto pulling code
        String code;
        code = input.nextLine();
        UserAuthResponse authResponse;
        try {
            authResponse = vk.oAuth()
                    .userAuthorizationCodeFlow(51434490, "dvCZlOdY5gtNvZgvUT8s", "http://localhost:8080/redirect.html", code)
                    .execute();
        } catch (ApiException | ClientException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return new User(authResponse.getUserId(), authResponse.getAccessToken());
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

    public static void turnNotifications(boolean turn, Group group, User user) {
        try {
            vk.wall().get(user).domain(group.getScreenName()).offset(0).count(3).execute();
        } catch (ClientException | ApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
