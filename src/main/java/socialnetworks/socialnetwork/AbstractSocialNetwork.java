package socialnetworks.socialnetwork;

import socialnetworks.socialnetwork.groups.SocialNetworkGroups;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import socialnetworks.socialnetwork.wall.SocialNetworkWall;

public abstract class AbstractSocialNetwork implements SocialNetwork {
    protected final SocialNetworkAuth oAuth;
    protected final SocialNetworkGroups groups;
    protected final SocialNetworkWall wall;

    protected AbstractSocialNetwork(SocialNetworkAuth oAuth, SocialNetworkGroups groups, SocialNetworkWall wall) {
        this.oAuth = oAuth;
        this.groups = groups;
        this.wall = wall;
    }
}
