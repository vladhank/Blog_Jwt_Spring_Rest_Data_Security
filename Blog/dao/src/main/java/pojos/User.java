package pojos;

import enums.RoleName;
import lombok.*;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import pojos.audit.DateAudit;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Entity
@Table
@OptimisticLocking(type = OptimisticLockType.VERSION)
@EqualsAndHashCode(callSuper = true)
public class User extends DateAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USER_ID")
    @Min(1L)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "USER_NAME", unique = true)
    @Access(AccessType.PROPERTY)
//    @Size(min =2, max = 50)
    private String username;

    @Column(name = "FIRST_NAME")
    @Access(AccessType.PROPERTY)
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$|^[а-яА-я]+(([',. -][а-яА-Я ])?[а-яА-Я]*)*$", message = "First name can only contain letters")
//    @Size(min = 2, max = 10, message = "First name should have at least 2 characters ")
    private String firstName;

    @Column(name = "LAST_NAME")
    @Access(AccessType.PROPERTY)
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$|^[а-яА-я]+(([',. -][а-яА-Я ])?[а-яА-Я]*)*$", message = "Last name can only contain letters")
    private String lastName;

    @Column(name = "PASSWORD")
    @Type(type = "string")
    @Access(AccessType.PROPERTY)
//    @Length(min = 7, max = 100)
    private String password;

    @Column(name = "EMAIL")
    @Type(type = "string")
    @Access(AccessType.PROPERTY)
    @Email
//    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    @Column(name = "IS_ACTIVE")
    private Boolean active;

    @Column(name = "IS_EMAIL_VERIFIED", nullable = false)
    private Boolean isEmailVerified;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "CREATED_AT", nullable = false)
    @Access(AccessType.PROPERTY)
    private LocalDateTime created;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();


    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Comment> comments = new ArrayList<>();


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Article> articles = new ArrayList<>();


    @Version
    private Integer version;

    public User(User user) {
        id = user.getId();
        firstName = user.firstName;
        lastName = user.lastName;
        password = user.password;
        email = user.email;
        roles = user.roles;
        active = user.active;
        created = user.created;
        isEmailVerified = user.isEmailVerified;
        comments = user.comments;
        articles = user.articles;
    }

    public void confirmVerification() {
        setIsEmailVerified(true);
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getUserList().add(this);
    }

    public void addRoles(Set<Role> roles) {
        roles.forEach(this::addRole);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUserList().remove(this);
    }

    public User() {
        super();
    }

    public User(String username, String firstName, String lastName, String password, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }

    public User(String username, String firstName, String lastName, String password, String email, Set<Role> roles, List<Comment> comments, List<Article> articles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.comments = comments;
        this.articles = articles;
        this.isEmailVerified = false;
        this.created = LocalDateTime.now();
    }


}
