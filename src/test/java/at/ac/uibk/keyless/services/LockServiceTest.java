package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.Lock;
import at.ac.uibk.keyless.models.UserRole;
import at.ac.uibk.keyless.repositories.KeyRepository;
import at.ac.uibk.keyless.repositories.LockRepository;
import at.ac.uibk.keyless.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
public class LockServiceTest {

  @Autowired
  LockService lockService;

  @Autowired
  LockRepository lockRepository;

  @Autowired
  KeyRepository keyRepository;

  @Autowired
  UserRepository userRepository;

  Lock localLock;


  @Before
  public void addLock() {
    Lock lock = new Lock();
    lock.setName("test");
    lock.setCode("test");
    lock.setAddress("test");
    lock.setRelevantKeys(new ArrayList<>());
    lock.setRelevantUsers(new ArrayList<>());
    localLock = lockRepository.save(lock);
  }

  @Test
  public void testGetLock() {
    // verify that lock has no users or keys to unlock it
    assertThat(keyRepository.findAll().stream()
      .anyMatch(k -> lockService.getLocksForKey(k.getKeyId()).contains(localLock)), is(false));
    assertThat(userRepository.findAll().stream()
      .filter(u -> !u.getRoles().contains(UserRole.ADMIN))
      .anyMatch(u -> lockService.getLocksForUser(u.getUserId()).contains(localLock)), is(false));

    assertThat(lockService.getLockForIdAndCode(localLock.getLockId(), localLock.getCode()).getName(),
      is(localLock.getName()));
  }

  @Test
  public void testAddRemovePermissionsFromLock() {
    List<Object> lockIds = new ArrayList<>();
    lockIds.add(localLock.getLockId());

    keyRepository.findAll()
      .forEach(k -> lockService.addKeysToLocks(lockIds, k.getKeyId()));
    userRepository.findAll()
      .forEach(u -> lockService.addUserToLocks(lockIds, u.getUserId()));

    assertThat(keyRepository.findAll().stream()
      .allMatch(k -> lockService.getLocksForKey(k.getKeyId()).contains(localLock)), is(true));
    assertThat(userRepository.findAll().stream()
      .allMatch(u -> lockService.getLocksForUser(u.getUserId()).contains(localLock)), is(true));

    keyRepository.findAll()
      .forEach(k -> lockService.removeKeyFromLocks(k.getKeyId()));
    userRepository.findAll()
      .forEach(u -> lockService.removeUserFromLocks(u.getUserId()));

    assertThat(keyRepository.findAll().stream()
      .anyMatch(k -> lockService.getLocksForKey(k.getKeyId()).contains(localLock)), is(false));
    assertThat(userRepository.findAll().stream()
      .filter(u -> !u.getRoles().contains(UserRole.ADMIN))
      .anyMatch(u -> lockService.getLocksForUser(u.getUserId()).contains(localLock)), is(false));
  }
}
