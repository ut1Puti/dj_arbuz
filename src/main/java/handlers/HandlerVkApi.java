package handlers;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiAccessException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.Group;
import user.User;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Scanner;

public class HandlerVkApi {
    private final static TransportClient transportClient = new HttpTransportClient();
    private final static VkApiClient vk = new VkApiClient(transportClient);

    public static User initUser(Scanner input) {
        //do auto pulling code
        WebTarget t = ClientBuilder.newClient().target("https://oauth.vk.com/authorize?client_id=51431012&display=page&redirect_uri={redirect_uri}&scope=groups&response_type={type}&v=5.131");
        System.out.println(t);
        System.out.println(t.resolveTemplate("redirect_uri", "https://oauth.vk.com/blank.html").resolveTemplate("type", "token").request().get());
        return new User(input.nextInt(), input.nextLine());
    }

    public static Group getMusicianGroup(String searchInfo, User user) {
        try {
            return vk.groups().search(user, searchInfo)
                    .offset(0).count(3)
                    .execute()
                    .getItems().get(0);
        } catch (ApiException | ClientException e){
            System.out.println("error:groups.search");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean joinGroup(int id, User user) {
        try {
            vk.groups().join(user).groupId(id).execute();
            return true;
        } catch (ClientException | ApiException e){
            System.out.println("error:group.join");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean notificationTurn(int id, User user){
        try{
            vk.groups()
        } catch (ClientException | ApiException e){
            System.out.println("error:group.join");
            e.printStackTrace();
        }
        return false;
    }
}
