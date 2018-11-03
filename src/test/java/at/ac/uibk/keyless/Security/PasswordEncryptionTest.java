package at.ac.uibk.keyless.Security;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.UserRepository;
import at.ac.uibk.keyless.Services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PasswordEncryptionTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Before
  public void addNewUser() {
    User newUser = new User();
    newUser.setEmail("newmail@keyless.com");
    newUser.setFirstName("newUser");
    newUser.setLastName("newUser");
    newUser.setCreatedAt(new Date());
    newUser.setPassword("password");
    userService.saveUser(newUser);
  }

  @Test
  public void testPasswordEncryption() {
    User newUser = userRepository.findFirstByEmail("newmail@keyless.com");
    assertThat(newUser.getPassword(), not("password"));
    assertThat(passwordEncoder.matches("password", newUser.getPassword()), is(true));
  }
}
