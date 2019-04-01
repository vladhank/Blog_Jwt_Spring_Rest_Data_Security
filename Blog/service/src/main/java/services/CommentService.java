package services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import pojos.Comment;
import repositories.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService extends IService<Comment> {

    Comment create(long userID, long articleID, String text);

    List<CommentDto> findAllCommentsForArticleWithPagination(long articleID, Pageable pageable);

    long getNumberOfAllCommentsForSingleArticle(long articleID);

    void delete(long commentID);

//    List<Comment> getCommentsForArticleWithOffsetAndLimit(long articleID, Pageable pageable);

}
