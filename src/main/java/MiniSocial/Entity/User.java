package MiniSocial.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String name;

    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;

    @NotEmpty
    @Size(min = 6)
    private String password;

    @Size(max = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Many-to-Many relationship with Group (users can join multiple groups)
    @ManyToMany(mappedBy = "members")
    private Set<Group> joinedGroups = new HashSet<>();

    // One-to-Many relationship with GroupJoinRequest (user can have multiple join requests)
    @OneToMany(mappedBy = "user")
    private Set<GroupJoinRequest> joinRequests = new HashSet<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Group> getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(Set<Group> joinedGroups) {
        this.joinedGroups = joinedGroups;
    }

    public Set<GroupJoinRequest> getJoinRequests() {
        return joinRequests;
    }

    public void setJoinRequests(Set<GroupJoinRequest> joinRequests) {
        this.joinRequests = joinRequests;
    }

    public enum Role {
        USER, ADMIN
    }
}
