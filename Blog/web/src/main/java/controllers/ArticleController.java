package controllers;

import enums.ArticleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pojos.Article;
import pojos.CustomUserDetails;
import pojos.wrapper.ArticleWrapper;
import repositories.dto.ArticleDto;
import services.ArticleService;
import services.exceptions.NotFoundException;
import validation.annotation.CurrentUser;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ArticleDto>> showAllPublicArticles(@RequestParam(value = "skip", defaultValue = "0") int skip,
                                                                  @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if (articleService.getNumberOfAllPosts() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(articleService.findAllByStatusWithPagination(ArticleStatus.PUBLIC, PageRequest.of(skip / limit, limit)), HttpStatus.OK);
        }

    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{articleID}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArticleDto> getSingleArticle(@PathVariable("articleID") long articleID) {
        ArticleDto articleDto = Optional.ofNullable(articleService.getSingleArticleDto(articleID))
                .orElseThrow(() -> new NotFoundException("No articles with id {" + articleID + "} were found"));
        return new ResponseEntity<>(articleDto, HttpStatus.OK);
    }

    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Article>> getAllArticlesForCurrentUser(@CurrentUser CustomUserDetails currentUser) {
        List<Article> articleList = articleService.findAllArticlesByUserID(currentUser.getId());
        if (articleList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(articleList, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "", consumes = "application/json")
    public ResponseEntity createArticle(@RequestBody ArticleWrapper article,
                                        @CurrentUser CustomUserDetails currentUser) {
        articleService.createArticle(article.getText(), article.getTitle(), currentUser.getId(), article.getTags());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "{articleID}", consumes = "application/json")
    public ResponseEntity updateArticle(@PathVariable("articleID") long articleID,
                                        @RequestBody ArticleWrapper articleWrapper,
                                        @CurrentUser CustomUserDetails currentUser) {
        Article articleFromDb = Optional.ofNullable(articleService.get(articleID))
                .orElseThrow(() -> new NotFoundException("No articles with id {" + articleID + "} were found"));
        if (articleFromDb.getUser().getId().equals(currentUser.getId())) {
            articleService.updateArticle(articleWrapper.getText(), articleWrapper.getTitle(), articleID);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/{articleID}")
    public ResponseEntity deletePost(@PathVariable("articleID") long articleID,
                                     @CurrentUser CustomUserDetails currentUser) {
        Article articleFromDb = Optional.ofNullable(articleService.get(articleID))
                .orElseThrow(() -> new NotFoundException("No articles with id {" + articleID + "} were found"));
        if (articleFromDb.getUser().getId().equals(currentUser.getId())) {
            articleService.delete(articleID);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

    }


}
