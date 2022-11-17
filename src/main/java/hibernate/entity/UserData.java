package hibernate.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity класс для взаимодействия Hibernate ORM и PostgreSQL(таблица user_data)
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "user_data", schema = "public", catalog = "users_database")
public class UserData {
    @Id
    @Column(name = "telegram_id", nullable = false, length = -1)
    private String telegramId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Basic
    @Column(name = "access_token", nullable = false, length = -1)
    private String accessToken;
}
