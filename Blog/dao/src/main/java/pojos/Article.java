package pojos;


import enums.ArticleStatus;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Min(1L)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    @Access(AccessType.PROPERTY)
    private Long articleID;

    @Column
    @Type(type = "string")
    @Access(AccessType.PROPERTY)
    @Length(min = 4, max = 20)
    private String title;


    @Column
    @Type(type = "string")
    @Access(AccessType.PROPERTY)
    @Length(min = 4, max = 2000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column
    private ArticleStatus status;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST}
    )
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "USER_ARTICLE_ID_FK"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User user;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "CREATED_AT", nullable = false)
    @Access(AccessType.PROPERTY)
    private LocalDateTime created;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "UPDATED_AT", nullable = false)
    @Access(AccessType.PROPERTY)
    private LocalDateTime updated;

    @OneToMany(
            mappedBy = "article",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "ARTICLE_TAGS",
            joinColumns = { @JoinColumn(name = "ARTICLE_ID") },
            inverseJoinColumns = { @JoinColumn(name = "TAG_ID") }
    )
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>();

    public Article(String title, String text, ArticleStatus status, User user, LocalDateTime createdAt, LocalDateTime updatedAt, List<Comment> comments, Set<Tag> tags) {
        this.title = title;
        this.text = text;
        this.status = status;
        this.user = user;
        this.created = createdAt;
        this.updated = updatedAt;
        this.comments = comments;
        this.tags = tags;
    }
}
