package handlers;

import com.vk.api.sdk.actions.Search;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.SearchType;
import com.vk.api.sdk.queries.groups.GroupsSearchQuery;
import com.vk.api.sdk.queries.groups.GroupsSetLongPollSettingsQuery;
import user.User;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Scanner;

public class HandlerVkApi {
    private final static TransportClient transportClient = new HttpTransportClient();
    private final static VkApiClient vk = new VkApiClient(transportClient);

    public static User initUser() throws ClientException, ApiException {
        //do auto pulling code
        WebTarget t = ClientBuilder.newClient().target("https://oauth.vk.com/authorize?client_id=51431012&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=groups&response_type={type}&v=5.131");
        System.out.println(t);
        System.out.println(t.resolveTemplate("type", "code").request().get());
        //System.out.println(t.resolveTemplate("type", "token").request().get());
        //
        UserAuthResponse authResponse = vk.oAuth()
                .userAuthorizationCodeFlow(51431012, "HFUwg4clnPm4vwlrPX9B", "https://t.me/oop_urfu_bot", new Scanner(System.in).nextLine())
                .execute();
        return new User(authResponse.getUserId(), authResponse.getAccessToken());
    }

    public static String getMusicianLink(String searchInfo, User user) throws ClientException, ApiException {
        return "https://vk.com/" + vk.groups().search(user, "lida")
                .offset(0).count(3)
                .execute()
                .getItems().get(0).getScreenName();
    }
}
