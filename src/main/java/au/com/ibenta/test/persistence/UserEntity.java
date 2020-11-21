package au.com.ibenta.test.persistence;

import lombok.Data;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@Table(name = "user")
@DynamicUpdate
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
