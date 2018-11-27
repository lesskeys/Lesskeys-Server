package at.ac.uibk.keyless.Security;

import at.ac.uibk.keyless.Models.*;
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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
  private User tenant2;
  private User visitor;
  private Lock main;
  private Lock tenants;

  @Before
  public void deleteAllLogs() {
    this.admin = userService.getUserById(1L);
    this.tenant = userService.getUserById(2L);
    this.tenant2 = userService.getUserById(3L);
    this.visitor = userService.getUserById(5L);
    this.main = lockService.getLockById(1L);
    this.tenants = lockService.getLockById(2L);

    logRepository.findAll()
      .forEach(l -> logRepository.delete(l));
    logRequestRepository.findAll()
      .forEach(r -> logRequestRepository.delete(r));
  }

  /**
   * Test that admin sees all three logs and himself as the actor and is also the owner of all of them
   */
  @Test
  public void testOwnLogVisible() {
    logService.logLoginEvent(admin, "Logged in manually");
    logService.logSystemEvent(admin, "Created User 2", tenant);
    logService.logUnlockEvent(main, "User 1", admin.getUserId());

    Set<SystemLogEntry> logs = logService.getEntriesForUser(admin.getUserId());
    assertThat(logs.size(), is(3));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().startsWith("User 1")), is(true));
    assertThat(logs.stream()
      .allMatch(l -> l.isOwner(admin)), is(true));
  }

  /**
   * Test that admin doesn't see others logs.
   * Test that admin sees unlocks for main lock without seeing the actor.
   */
  @Test
  public void testOtherLogInvisible() {
    logService.logLoginEvent(tenant, "Logged in manually");
    logService.logUnlockEvent(main, "User 2", tenant.getUserId());
    logService.logUnlockEvent(main, "User 3", tenant2.getUserId());

    Set<SystemLogEntry> logs = logService.getEntriesForUser(admin.getUserId());
    assertThat(logs.size(), is(2));
    assertThat(logs.stream()
      .filter(l -> !l.getType().equals(SystemLogType.UNLOCK))
      .collect(Collectors.toList()).size(), is(0));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().equals("")), is(true));
  }

  /**
   * Test that admin sees others logs if they accepted the request.
   */
  @Test
  public void testRequestedLogVisible() {
    logService.logLoginEvent(tenant, "Logged in manually");
    logService.logLoginEvent(tenant2, "Logged in manually");
    logService.logUnlockEvent(main, "User 2", tenant.getUserId());
    logService.logUnlockEvent(tenants, "User 2", tenant.getUserId());
    logService.logUnlockEvent(main, "User 3", tenant2.getUserId());

    Set<SystemLogEntry> logs = logService.getEntriesForUser(admin.getUserId());
    assertThat(logs.size(), is(2));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().equals("")), is(true));

    logRequestService.createRequest(main, new Date(), "test", false);
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().equals("")), is(true));

    SystemLogRequest lockRequest = logRequestService.getById(1L);

    // only tenant accepts request
    logRequestService.acceptRequestForUser(lockRequest, tenant);
    logs = logService.getEntriesForUser(admin.getUserId());
    assertThat(logs.stream()
      .anyMatch(l -> l.getActor().equals("")), is(true));

    // visitor also accepts request
    logRequestService.acceptRequestForUser(lockRequest, tenant2);
    logs = logService.getEntriesForUser(admin.getUserId());
    assertThat(logs.stream()
      .anyMatch(l -> l.getActor().equals("")), is(false));
    // admin sees the actors of the other unlocks
    assertThat(logs.stream()
      .filter(l -> l.getType().equals(SystemLogType.UNLOCK))
      .allMatch(l -> l.getActor().startsWith("User")), is(true));


    logRequestService.createRequest(tenant.getUserId(), new Date(), SystemLogType.LOGIN, "test", false);
    SystemLogRequest loginRequest = logRequestService.getById(2L);

    logRequestService.acceptRequestForUser(loginRequest, tenant);
    logs = logService.getEntriesForUser(admin.getUserId());
    // admin sees the login logs he has requested
    assertThat(logs.stream()
      .filter(l -> l.getType().equals(SystemLogType.LOGIN))
      .collect(Collectors.toList()).size(), is(1));
    assertThat(logs.stream()
      .filter(l -> l.getType().equals(SystemLogType.LOGIN))
      .allMatch(l -> l.getActor().startsWith("User 2")), is(true));
    assertThat(logs.size(), is(4));
  }

  /**
   * Test if a users sees its sub-users, only visitors, log.
   * Test if visitor sees only his own log.
   */
  @Test
  public void testSubUserVisible() {
    logService.logLoginEvent(visitor, "Logged in manually");
    logService.logUnlockEvent(main, "User 5", visitor.getUserId());

    Set<SystemLogEntry> logs = logService.getEntriesForUser(tenant.getUserId());
    assertThat(logs.size(), is(2));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().startsWith("User 5")), is(true));

    logService.logUnlockEvent(main, "User 2", tenant.getUserId());
    logs = logService.getEntriesForUser(tenant.getUserId());
    assertThat(logs.size(), is(3));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().startsWith("User")), is(true));
    assertThat(logs.stream()
      .anyMatch(l -> l.getActor().startsWith("User 2")), is(true));

    logs = logService.getEntriesForUser(visitor.getUserId());
    assertThat(logs.size(), is(2));
    assertThat(logs.stream()
      .allMatch(l -> l.getActor().startsWith("User 5")), is(true));
  }
}
