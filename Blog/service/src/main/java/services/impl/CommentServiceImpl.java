package services.impl;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Comment;
import repositories.ArticleRepository;
import repositories.CommentRepository;
import repositories.UserRepository;
import repositories.dto.CommentDto;
import services.CommentService;
import services.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service("commentService")
@Transactional
@NoArgsConstructor
public class CommentServiceImpl extends BaseService<Comment> implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CommentDto> findAllCommentsForArticleWithPagination(long articleID, Pageable pageable) {
        return Optional.ofNullable(commentRepository.findAllCommentsForArticleWithPagination(articleID, pageable))
                .orElseThrow(() -> new NotFoundException("No articles with id {" + articleID + "} were found"));
    }

    public long getNumberOfAllCommentsForSingleArticle(long articleID) {
        if (articleRepository.findById(articleID).isPresent()) {
            return commentRepository.getNumberOfAllCommentsForSingleArticle(articleID);
        } else {
            throw new NotFoundException("No article with id {" + articleID + "} were found");
        }
    }

    public Comment create(long userID, long articleID, String text) {

        return commentRepository.save(new Comment(userRepository.getOne(userID), articleRepository.getOne(articleID), text, LocalDateTime.now()));
    }

    public void delete(long commentID) {

        if (( commentRepository.findById(commentID) ).isPresent()) {
            commentRepository.deleteById(commentID);
        } else {
            throw new NotFoundException("No comment with id {" + commentID + "} were found");
        }
    }

}
