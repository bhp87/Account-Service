package account.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    private List<Payment> payments = new ArrayList<>();
    @Column(nullable = false)
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "roles", nullable = false)
    private List<String> roles;
    @Column(name = "failed_attempt")
    private int failedAttempt = 0;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }


    public boolean isEnabled() {
        return true;
    }

    public boolean containsRole(String roleName) {
        for (String role : roles) {
            if (role.equals(roleName))
                return true;
        }
        return false;
    }
}