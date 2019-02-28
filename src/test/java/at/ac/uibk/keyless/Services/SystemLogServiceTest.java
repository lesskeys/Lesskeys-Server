package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class SystemLogServiceTest {

  @Autowired
  SystemLogService systemLogService;

  @Autowired
  UserService userService;

  @Autowired
  SystemLogRequestService logRequestService;

  @Autowired
  LockService lockService;


  @Before
  public void addLog() {
    systemLogService.logSystemEvent(userService.getUserById(1L), "test", null);
  }

  @Test
  public void testLogService() {
    List<SystemLogEntry> log = systemLogService.getAll();
    assertThat(log, is(notNullValue()));

    assertThat(log.stream()
      .anyMatch(e -> e.getEvent().endsWith("test")), is(true));
  }
  /* Excluded for Jenkins
  @Test
  public void testLogRequestService() {
    Lock lock = lockService.getLockById(1L);
    User user = userService.getUserById(1L);
    logRequestService.createRequest(lock, new Date(), "Message", true);

    assertThat(logRequestService.getRequestsForUser(user).size(), is(greaterThanOrEqualTo(1)));
  }*/
}
