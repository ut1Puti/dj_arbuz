package hibernate.entity;

import jakarta.persistence.*;

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
}
