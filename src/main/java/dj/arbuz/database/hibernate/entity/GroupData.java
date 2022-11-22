package dj.arbuz.database.hibernate.entity;

import com.vk.api.sdk.objects.users.User;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity класс для взаимодействия Hibernate ORM и PostgreSQL(таблица group_data)
 */
@EqualsAndHashCode
@Entity
@Table(name = "group_data", schema = "public", catalog = "users_database")
public class GroupData {
    @Getter
    @Setter
    @Id
    @Column(name = "group_name", nullable = false, length = -1)
    private String groupName;

    @Getter
    @Setter
    @Basic
    @Column(name = "date_last_post", nullable = false)
    private Long dateLastPost;

    @Getter
    @Setter
    @Basic
    @Column(name = "users", nullable = false, length = -1)
    private String users;

    @ManyToMany(mappedBy = "subscribedGroups")
    private Set<UserData> subscribedUsers = new HashSet<>();

    public Set<UserData> getSubscribedUsers() {
        return Set.copyOf(subscribedUsers);
    }

    public void setSubscribedUsers(Collection<UserData> subscribedUsers) {
        this.subscribedUsers.addAll(subscribedUsers);
    }
}
