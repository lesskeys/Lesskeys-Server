package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.User;
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
    String token = sessionService.initSession(admin);
    assertThat(sessionService.isValidSession(token), is(true));
    assertThat(sessionService.userMatchesSession(token, userId), is(true));
    assertThat(sessionService.initSession(admin), is(not(token)));
    assertThat(sessionService.userMatchesSession(token, userId), is(false));
  }
}
