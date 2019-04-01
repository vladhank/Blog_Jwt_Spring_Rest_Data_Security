package services.impl;

import enums.ArticleStatus;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojos.Article;
import pojos.Comment;
import pojos.Tag;
import repositories.ArticleRepository;
import repositories.CommentRepository;
import repositories.TagRepository;
import repositories.UserRepository;
import repositories.dto.ArticleDto;
import services.ArticleService;
import services.exceptions.NotFoundException;
import services.exceptions.ServiceException;

import javax.validation.ConstraintViolationException;
import java.nio.file.OpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service("articleService")
@Transactional
@NoArgsConstructor
public class ArticleServiceImpl extends BaseService<Article> implements ArticleService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TagRepository tagRepository;

    @Override
    public Article createArticle(String text, String title, long userID, List<Tag> tags) {

//        Tag tag = Optional.ofNullable(tagRepository.findByName(tagName)).orElseGet(() -> new Tag(tagName));
//        tags.forEach((tag)->Optional.ofNullable(tagRepository.findByName(tag.getName())).orElseGet(() -> new Tag(tag.getName())));
        Article article = new Article();
        article.setText(text);
        article.setTitle(title);
        article.setUser(userRepository.getOne(userID));
        article.setStatus(ArticleStatus.PUBLIC);
        article.setCreated(LocalDateTime.now());
        article.setUpdated(LocalDateTime.now());
        article.getTags().addAll(tags);
        return articleRepository.save(article);

    }

    @Override
    public Article updateArticle(String text, String title, long articleID) {

        Article article = articleRepository.getOne(articleID);
        article.setTitle(title);
        article.setText(text);
        article.setUpdated(LocalDateTime.now());
        return articleRepository.save(article);

    }

    @Override
    public ArticleDto getSingleArticleDto(long articleID) {

        return Optional.ofNullable(articleRepository.getSingleArticleDto(articleID))
                .orElseThrow(() -> new NotFoundException("No articles with id {" + articleID + "} were found"));
    }

    @Override
    public void delete(long articleID) {

        if (articleRepository.findById(articleID).isPresent()) {
            articleRepository.deleteById(articleID);
        } else {
            throw new NotFoundException("No articles with id {" + articleID + "} were found");
        }

    }

    @Override
    public long getNumberOfAllPosts() {
        return articleRepository.getNumberOfAllPosts();
    }

    @Override
    public List<ArticleDto> findAllByStatusWithPagination(Enum status, Pageable pageable) {

        return Optional.ofNullable(articleRepository.findAllByStatusWithPagination(status, pageable))
                .orElseThrow(() -> new NotFoundException("No articles with status {" + status.toString() + "} were found"));
    }

    @Override
    public List<Tag> findAllTagsForArticle(long articleID) {

        return Optional.ofNullable(articleRepository.findAllTagsForArticle(articleID))
                .orElseThrow(() -> new NotFoundException("No tags for article with {" + articleID + "} ID were found"));
    }

    @Override
    public List<Article> findAllArticlesByUserID(long userID) {

        return articleRepository.findAllByUserID(userID);

    }

    @Override
    public List<Comment> findAllCommentsForArticle(long articleID) {

        return articleRepository.findAllCommentsForArticle(articleID);
    }

    @Override
    public List<Article> findAllArticlesByTag(String tagName) {

        return articleRepository.findAllArticlesByTag(tagName);
    }

}
