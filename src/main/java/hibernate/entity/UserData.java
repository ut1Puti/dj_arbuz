package hibernate.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Entity класс для взаимодействия Hibernate ORM и PostgreSQL(таблица user_data)
 */
@Entity
@Table(name = "user_data", schema = "public", catalog = "users_database")
public class UserData {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "telegram_id", nullable = false, length = -1)
    private String telegramId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Basic
    @Column(name = "access_token", nullable = false, length = -1)
    private String accessToken;
    @Basic
    @Column(name = "covered_access_token", nullable = false, length = -1)
    private String coveredAccessToken;

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCoveredAccessToken() {
        return coveredAccessToken;
    }

    public void setCoveredAccessToken(String coveredAccessToken) {
        this.coveredAccessToken = coveredAccessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equals(telegramId, userData.telegramId) && Objects.equals(userId, userData.userId) && Objects.equals(accessToken, userData.accessToken) && Objects.equals(coveredAccessToken, userData.coveredAccessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telegramId, userId, accessToken, coveredAccessToken);
    }
}
