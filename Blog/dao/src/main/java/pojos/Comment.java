package pojos;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "COMMENT")
public class Comment implements Serializable {

    @Id
    @Min(1L)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    @Access(AccessType.PROPERTY)
    private Long commentID;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "USER_COMMENT_ID_FK"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ARTICLE_ID", foreignKey = @ForeignKey(name = "ARTICLE_COMMENT_ID_FK"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Article article;

    @Column
    @Type(type = "string")
    @Access(AccessType.PROPERTY)
    @Length(min = 1, max = 1000)
    private String text;

    @Column
    @Access(AccessType.PROPERTY)
    private LocalDateTime createdAt;

    public Comment(User user, Article article, String text, LocalDateTime createdAt) {
        this.user = user;
        this.article = article;
        this.text = text;
        this.createdAt = createdAt;
    }
}
