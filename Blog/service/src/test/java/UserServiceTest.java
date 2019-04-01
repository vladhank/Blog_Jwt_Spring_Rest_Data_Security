import config.ServiceConfiguration;
import configuration.Audit;
import enums.RoleName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojos.Article;
import pojos.Role;
import pojos.Tag;
import pojos.User;
import services.ArticleService;
import services.RoleService;
import services.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfiguration.class, Audit.class})
@EnableJpaRepositories
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RoleService roleService;

    @Test
    public void userSaveTest() {

        Role adminRole = new Role(RoleName.ROLE_ADMIN);

        roleService.save(adminRole);
//        User user = new User("vumoak","Nikola","Tesla","88888888","acdc@gmail.com",new HashSet<>(),new ArrayList<>(),new ArrayList<>());
        User user = new User("vsdvds","fdsfsdf","sfsdfafa","88888888","adasda@gmal.com");
        user.getRoles().add(adminRole);
        userService.save(user);
        List<Tag> tagList = Arrays.asList(new Tag("mohjo"));
        articleService.createArticle("Lorem ipsum text","Lorem title",user.getId(),tagList);
        System.out.println();

    }

}
