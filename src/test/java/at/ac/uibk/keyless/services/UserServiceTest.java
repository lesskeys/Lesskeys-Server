package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.User;
import at.ac.uibk.keyless.models.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;


  @Test
  public void testUserSaving() {
    User admin = userService.getUserByEmail("admin@keyless.com");
    assertThat(admin.getPassword(), is(not("test")));
    String hashedPassword = admin.getPassword();
    userService.saveUser(admin);
    assertThat(userService.getUserById(admin.getUserId()).getPassword(), is(hashedPassword));

    assertThat(userService.hasRole(admin, "Admin"), is(true));
  }

  @Test
  public void testUserRoleGetter() {
    assertThat(userService.getRoleForString("Admin"), is(UserRole.ADMIN));
    assertThat(userService.getRoleForString("Tenant"), is(UserRole.TENANT));
    assertThat(userService.getRoleForString("Custodian"), is(UserRole.CUSTODIAN));
    assertThat(userService.getRoleForString("Visitor"), is(UserRole.VISITOR));
  }

  @Test
  public void testPasswordChange() {
    User admin = userService.getUserByEmail("admin@keyless.com");
    assertThat(userService.editUsersPassword(admin, "admin", "test", "test"), is("Success"));
    assertThat(passwordEncoder.matches("test", userService.getUserById(admin.getUserId()).getPassword()),
      is(true));
    assertThat(userService.editUsersPassword(admin, "test", "wrong", "more_wrong"),
      is("Failure"));
  }
}
