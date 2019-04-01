import config.ServiceConfiguration;
import configuration.Audit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojos.Article;
import pojos.Tag;
import services.ArticleService;
import services.TagService;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfiguration.class, Audit.class})
@EnableJpaRepositories
@Transactional
public class TagServiceList {

    @Autowired
    TagService tagService;

    @Autowired
    ArticleService articleService;

    @Test
    public void tagSaveTest() {
//        Tag tag = tagService.save(new Tag("Mohave"));
        Article article = articleService.get(1L);
        tagService.get(1L).getArticles().add(article);
//      tag.getArticles().add(articleService.get(1L));
//        tagService.update(tag);
        System.out.println(article);
    }
}
