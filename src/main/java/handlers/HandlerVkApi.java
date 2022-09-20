package handlers;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class HandlerVkApi {
    public static boolean initUser() throws ClientException, ApiException{
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        //do auto pulling code
        WebTarget t = ClientBuilder.newClient().target("https://oauth.vk.com/authorize?client_id=51431012&display=page&redirect_uri=https://t.me/oop_urfu_bot&response_type=code&v=5.131");
        System.out.println(t);
        Response r = t.request().get();
        System.out.println(r);
        //
        UserAuthResponse authResponse = vk.oAuth()
                .userAuthorizationCodeFlow(51431012, "HFUwg4clnPm4vwlrPX9B", "https://t.me/oop_urfu_bot", "5319c7d419125ff8ab")
                .execute();
        return true;
    }

    public static String getMusicianLink(String searchInfo){
        return "link";
    }
}
