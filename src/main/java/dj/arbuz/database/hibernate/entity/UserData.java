package dj.arbuz.database.hibernate.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity класс для взаимодействия Hibernate ORM и PostgreSQL(таблица user_data)
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "user_data", schema = "public", catalog = "users_database")
public class UserData {
    @Getter
    @Setter
    @Id
    @Column(name = "telegram_id", nullable = false, length = -1)
    private String telegramId;

    @Getter
    @Setter
    @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Getter
    @Setter
    @Basic
    @Column(name = "access_token", nullable = false, length = -1)
    private String accessToken;

    @ManyToMany
    @JoinTable(name = "subscribers", joinColumns = @JoinColumn(name = "subs"), inverseJoinColumns = @JoinColumn(name = "sub_group"))
    private Set<GroupData> subscribedGroups = new HashSet<>();

    public Set<GroupData> getSubscribedGroups() {
        return Set.copyOf(subscribedGroups);
    }

    public void setSubscribedGroups(Collection<GroupData> subscribedGroups) {
        this.subscribedGroups.addAll(subscribedGroups);
    }
}
