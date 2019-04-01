package repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pojos.Comment;
import repositories.dto.CommentDto;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT new repositories.dto.CommentDto(" +
            "c.commentID,c.user.username,c.text,c.createdAt) " +
            "FROM Comment c JOIN c.user WHERE c.article.id=:articleID ORDER BY c.createdAt DESC")
    List<CommentDto> findAllCommentsForArticleWithPagination(@Param("articleID") long articleID, Pageable pageable);

    @Query("SELECT COUNT(*) FROM Comment c WHERE c.article.id=:articleID")
    long getNumberOfAllCommentsForSingleArticle(@Param("articleID") long articleID);

    List<Comment> findAll();

}
