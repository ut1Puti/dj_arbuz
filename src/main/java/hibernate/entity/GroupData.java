package hibernate.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "group_data", schema = "public", catalog = "users_database")
public class GroupData {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "group_name", nullable = false, length = -1)
    private String groupName;
    @Basic
    @Column(name = "date_last_post", nullable = false)
    private Long dateLastPost;
    @Basic
    @Column(name = "users", nullable = false, length = -1)
    private String users;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getDateLastPost() {
        return dateLastPost;
    }

    public void setDateLastPost(Long dateLastPost) {
        this.dateLastPost = dateLastPost;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupData groupData = (GroupData) o;
        return Objects.equals(groupName, groupData.groupName) && Objects.equals(dateLastPost, groupData.dateLastPost) && Objects.equals(users, groupData.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, dateLastPost, users);
    }
}
