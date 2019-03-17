package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class SessionServiceTest {

  @Autowired
  SessionService sessionService;

  @Autowired
  UserService userService;


  @Test
  public void testSessionService() {
    long userId = 1L;
    User admin = userService.getUserById(userId);
    User user2 = userService.getUserById(2L);
    String token = sessionService.initSession(admin, "test");
    assertThat(sessionService.isValidSession(token), is(true));
    assertThat(sessionService.userMatchesSession(token, userId), is(true));

    sessionService.initSession(user2, "test");
    assertThat(sessionService.userMatchesSession(token, 2L), is(false));
    assertThat(sessionService.userMatchesValidSession(token, admin.getEmail()), is(true));
    assertThat(sessionService.getByUserId(userId).getSessionToken(), is(token));

    // test that old token is not valid anymore
    assertThat(sessionService.initSession(admin, "test"), is(not(token)));
    assertThat(sessionService.userMatchesSession(token, userId), is(false));
  }
}
