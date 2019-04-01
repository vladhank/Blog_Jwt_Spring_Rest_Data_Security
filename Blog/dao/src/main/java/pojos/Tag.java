package pojos;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Min(1L)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "long")
    @Access(AccessType.PROPERTY)
    private Long tagID;

    @Column
    @Type(type = "string")
    @Access(AccessType.PROPERTY)
    @Length(min = 4, max = 20)
    private String name;

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Article> articles = new ArrayList<>();

    public Tag(String name) {
        this.name = name;
    }

}
