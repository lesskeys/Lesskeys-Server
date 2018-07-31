package at.ac.uibk.keyless;

import at.ac.uibk.keyless.repositories.UserRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DataInitializationTest {

  @Autowired
  UserRepository userRepository;

  @Test
  public void testDataInitialization() {
    assertThat(userRepository.findAll().size(), is(greaterThan(0)));
    assertThat(userRepository.findFirstByEmail("admin@keyless.com"), is(notNullValue()));
  }
}
