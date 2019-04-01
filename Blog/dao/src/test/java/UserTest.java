//import config.HikariPoolConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes=HikariPoolConfig.class)
@Transactional
public class UserTest {

    @PersistenceContext(name = "entityManagerFactory")
    EntityManager entityManager;

    @Test
    public void doNothing(){

    }
}
