package socialnetworks.socialnetwork;

import socialnetworks.socialnetwork.groups.SocialNetworkGroups;
import socialnetworks.socialnetwork.oAuth.SocialNetworkAuth;
import socialnetworks.socialnetwork.wall.SocialNetworkWall;

public abstract class AbstractSocialNetwork<G, P, U, S, A> implements SocialNetwork<U> {
    protected final SocialNetworkAuth<S, U> oAuth;
    protected final SocialNetworkGroups<G, U> groups;
    protected final SocialNetworkWall<P, A> wall;

    protected AbstractSocialNetwork(SocialNetworkAuth<S, U> oAuth, SocialNetworkGroups<G, U> groups, SocialNetworkWall<P, A> wall) {
        this.oAuth = oAuth;
        this.groups = groups;
        this.wall = wall;
    }
}
