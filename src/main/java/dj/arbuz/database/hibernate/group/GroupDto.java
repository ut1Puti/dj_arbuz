package dj.arbuz.database.hibernate.group;

import dj.arbuz.database.hibernate.user.UserDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entity класс для взаимодействия Hibernate ORM и PostgreSQL(таблица group_data)
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"subscribedUsers"})
@Entity(name = "group_data")
@Table(schema = "public", catalog = "users_database")
public class GroupDto {
    @Id
    @Column(name = "group_name", nullable = false, length = -1)
    private String groupName;

    @Column(name = "date_last_post", nullable = false)
    private Long dateLastPost;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "subscribers",
            joinColumns = @JoinColumn(name = "group_name"),
            inverseJoinColumns = @JoinColumn(name = "user_telegram_id"))
    private List<UserDto> subscribedUsers;

    public void addNewSubscriber(UserDto subscriber) {
        subscribedUsers.add(subscriber);
    }
}
