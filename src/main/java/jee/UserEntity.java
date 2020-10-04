package jee;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;


@Data
@Entity
@Table(name = "Users")
@Builder
public class UserEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private Boolean isPrivileged;
    private Timestamp lastLogin;

    public UserEntity() {
    }

    public UserEntity(Integer arg1, String arg2, String arg3, boolean arg4, Timestamp arg5) {
        this.id = arg1;
        this.username = arg2;
        this.password = arg3;
        this.isPrivileged = arg4;
        this.lastLogin = arg5;
    }
    
    public UserEntity(String arg1, String arg2, boolean arg3) {
        this.username = arg1;
        this.password = arg2;
        this.isPrivileged = arg3;
    }
}