import config.ServiceConfiguration;
import configuration.Audit;
import enums.ArticleStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojos.Article;
import pojos.User;
import services.ArticleService;
import services.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfiguration.class, Audit.class})
@EnableJpaRepositories
@Transactional
public class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Test
    public void articleSaveTest() {
        User user = userService.get(1L);
        Article article = articleService
                .save(new Article("Test title", "Lorem ipsum", ArticleStatus.PRIVATE, user, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new HashSet<>()));
        Assert.assertNotNull(articleService.get(article.getArticleID()));
    }


}

