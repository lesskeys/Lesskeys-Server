package at.ac.uibk.keyless.Security;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.LockRepository;
import at.ac.uibk.keyless.Repositories.SystemLogEntryRepository;
import at.ac.uibk.keyless.Repositories.SystemLogRequestRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SystemLogRequestService;
import at.ac.uibk.keyless.Services.SystemLogService;
import at.ac.uibk.keyless.Services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class SystemLogPrivacyTest {

  @Autowired
  SystemLogService logService;

  @Autowired
  SystemLogRequestService logRequestService;

  @Autowired
  SystemLogEntryRepository logRepository;

  @Autowired
  SystemLogRequestRepository logRequestRepository;

  @Autowired
  UserService userService;

  @Autowired
  LockService lockService;


  private User admin;
  private User tenant;
  private User visitor;
  private Lock main;

  @Before
  public void deleteAllLogs() {
    this.admin = userService.getUserById(1L);
    this.tenant = userService.getUserById(2L);
    this.visitor = userService.getUserById(5L);
    this.main = lockService.getLockById(1L);
    logRepository.findAll()
      .forEach(l -> logRepository.delete(l));
    logRequestRepository.findAll()
      .forEach(r -> logRequestRepository.delete(r));
  }

  @Test
  public void testOwnLogVisible() {
    logService.logLoginEvent(admin, "Logged in manually");
    logService.logSystemEvent(admin, "Created User 2", tenant);
    logService.logUnlockEvent(main, "User 1");

    Set<SystemLogEntry> logs = logService.getEntriesForUser(admin.getUserId());
    // assert that admin sees all three logs and himself as the actor and is also the owner of all of them
    assertThat(logs.size(), is(3));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().startsWith("User 1")), is(true));
    assertThat(logs.stream()
      .allMatch(l -> l.isOwner(admin)), is(true));
  }
}
