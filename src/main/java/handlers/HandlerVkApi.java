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
import java.util.List;
import java.util.Scanner;

import static com.vk.api.sdk.objects.groups.Fields.VERIFIED;

public class HandlerVkApi {
    private final static TransportClient transportClient = new HttpTransportClient();
    private final static VkApiClient vk = new VkApiClient(transportClient);

    public static User initUser() {
        //do auto pulling code
        WebTarget t = ClientBuilder.newClient().target("https://oauth.vk.com/authorize?client_id=51431012&display=page&redirect_uri={redirect_uri}&scope=270336&response_type={type}&v=5.131");
        System.out.println(t);
        System.out.println(t.resolveTemplate("redirect_uri", "https://oauth.vk.com/blank.html").resolveTemplate("type", "token").request().get());
        //пока чтоб запустить эту пупу лупу надо перейти по ссылке и скопировать в конце адрессной строки user_id а потом access_token
        //только то что после знака равно и ввести в том порядке в котором перечислено
        return new User(new Scanner(System.in).nextInt(), new Scanner(System.in).nextLine());
    }

    public static List<Group> searchGroups(String groupName, User user) {
        try {
            return vk.groups().search(user, groupName)
                    .offset(0).count(3)
                    .execute()
                    .getItems();
        } catch (ApiException | ClientException e){
            System.out.println("error:HandlerVkApi:groups.search");
        }
        return null;
    }

    public static Group searchVerifiedGroup(String groupName, User user){
        List<Group> foundGroups = searchGroups(groupName, user);
        if (foundGroups == null)
            return null;
        try {
            for (Group group : foundGroups){
                List<GetByIdObjectLegacyResponse> foundVerifiedGroups = vk.groups().getByIdObjectLegacy(user)
                        .groupId(String.valueOf(group.getId()))
                        .fields(VERIFIED)
                        .execute();
                if (foundVerifiedGroups.size() == 1)
                    return foundVerifiedGroups.get(0);
            }
        } catch (ApiException | ClientException e){
            System.out.println("error:HandlerVkApi:groups.search");
        }
        return null;
    }

    public static boolean joinGroup(String groupName, User user) {
        Group group = searchVerifiedGroup(groupName, user);
        if (group == null)
            return false;
        if (group.isMember())
            return true;
        try {
            vk.groups().join(user).groupId(group.getId()).execute();
            return true;
        } catch (ClientException | ApiException e) {
            System.out.println("error:HandlerVkApi:group.join");
        }
        return false;
    }

    public static void turnNotifications(boolean turn, Group group, User user) {
        try {
            vk.wall().get(user).domain(group.getScreenName()).offset(0).count(3).execute();
        } catch (ClientException | ApiException e) {
            System.out.println("error:HandlerVkApi:group.join");
        }
    }
}
