package repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pojos.Article;
import pojos.Comment;
import pojos.Tag;
import repositories.dto.ArticleDto;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a JOIN a.user u WHERE u.id=:userID")
    List<Article> findAllByUserID(@Param("userID") long userID);

    @Query("SELECT COUNT(*) FROM Article ")
    long getNumberOfAllPosts();

    Article findArticleByArticleID(Long id);

    @Query("SELECT new repositories.dto.ArticleDto(" +
            "a.articleID,a.title,a.text,a.created,a.updated,a.user.username) " +
            "FROM Article a WHERE a.articleID=:articleID")
    ArticleDto getSingleArticleDto(@Param("articleID") long articleID);

    @Query("SELECT new repositories.dto.ArticleDto(" +
            "a.articleID,a.title,a.text,a.created,a.updated,a.user.username) " +
            "FROM Article a JOIN a.user WHERE a.status=:status ORDER BY a.updated DESC")
    List<ArticleDto> findAllByStatusWithPagination(@Param("status") Enum status, Pageable pageable);

//    @Query("SELECT new repositories.dto.ArticleDTO(" +
//            "a.articleID,a.title,a.text,a.created,a.updated,a.user.username) "  +
//            "FROM Article a JOIN a.user WHERE a.user.userID=:userID")
//    List<ArticleDto> findAllArticlesDtoForUserById(@Param("userID")Long userID);

    @Query("SELECT a.tags FROM Article a WHERE a.articleID=:articleID")
    List<Tag> findAllTagsForArticle(@Param("articleID") long articleID);

    @Query("SELECT a.comments FROM Article a WHERE a.articleID=:articleID")
    List<Comment> findAllCommentsForArticle(@Param("articleID") long articleID);

    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.name=:name")
    List<Article> findAllArticlesByTag(@Param("name") String tagName);

}
