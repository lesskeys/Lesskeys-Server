package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class LockControllerTest {

  @Autowired
  private LockController lockController;

  @Autowired
  SessionService sessionService;

  @Autowired
  UserService userService;


  private final String lockId = "1";
  private final String code = "code";

  private Map<String, String> arg = new HashMap<>();

  @Before
  public void setup() {
    arg.put("lockId", lockId);
    arg.put("code", code);

    long userId = 1L;
    User admin = userService.getUserById(userId);
    sessionService.initSession(admin, "test");
  }

  @Test
  public void testGetKeys() {
    List<Map<String, Object>> data = lockController.getKeysForLock(arg);
    assertThat(data, is(not(empty())));
    assertThat(data.get(0).get("content"), is(not(nullValue())));
  }

  @Test
  public void testGetUsers() {
    List<Map<String, Object>> data = lockController.getSessionsForLock(arg);
    assertThat(data, is(not(empty())));
    assertThat(data.get(0).get("username").toString(), startsWith("admin"));
  }
}
