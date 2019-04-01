package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repositories.dto.CommentDto;
import services.ArticleService;
import services.CommentService;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping(value = "/{articleId}/comments", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CommentDto>> showAllCommentsWithPagination(@PathVariable Long articleId,
                                                                          @RequestParam(value = "skip", defaultValue = "0") int skip,
                                                                          @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if (commentService.getNumberOfAllCommentsForSingleArticle(articleId) == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(commentService.findAllCommentsForArticleWithPagination(articleId, PageRequest.of(skip / limit, limit)), HttpStatus.OK);
        }

    }

}
