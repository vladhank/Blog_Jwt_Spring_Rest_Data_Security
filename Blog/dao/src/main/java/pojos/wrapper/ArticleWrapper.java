package pojos.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pojos.Tag;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleWrapper {
    private String title;
    private String text;
    private List<Tag> tags;
}
