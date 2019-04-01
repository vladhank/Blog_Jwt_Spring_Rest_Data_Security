package services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import pojos.Article;
import pojos.Comment;
import pojos.Tag;
import repositories.dto.ArticleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService extends IService<Article> {

    Article createArticle(String text, String title, long userID, List<Tag> tags);

    ArticleDto getSingleArticleDto(long articleID);

    Article updateArticle(String text, String title, long articleID);

    void delete(long articleID);

    long getNumberOfAllPosts();

    List<ArticleDto> findAllByStatusWithPagination(Enum status, Pageable pageable);

    List<Tag> findAllTagsForArticle(long articleID);

    List<Article> findAllArticlesByUserID(long userID);

    List<Comment> findAllCommentsForArticle(long articleID);

    List<Article> findAllArticlesByTag(String tagName);

}
