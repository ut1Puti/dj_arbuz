package dj.arbuz.database.hibernate.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity класс для взаимодействия Hibernate ORM и PostgreSQL(таблица group_data)
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "group_data", schema = "public", catalog = "users_database")
public class GroupData {
    @Id
    @Column(name = "group_name", nullable = false, length = -1)
    private String groupName;
    @Basic
    @Column(name = "date_last_post", nullable = false)
    private Long dateLastPost;
    @Basic
    @Column(name = "users", nullable = false, length = -1)
    private String users;
}
