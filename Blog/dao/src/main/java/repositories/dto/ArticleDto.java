package repositories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pojos.Comment;
import pojos.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ArticleDto {
    private Long articleID;
    private String title;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private Set<Tag> tagList = new HashSet<>();
    private List<Comment> commentList = new ArrayList<>();


    public ArticleDto(Long articleID, String title, String text, LocalDateTime createdAt, LocalDateTime updatedAt, String username) {
        this.articleID = articleID;
        this.title = title;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.username = username;
    }
}
