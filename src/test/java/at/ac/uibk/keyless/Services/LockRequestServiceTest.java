package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.UnlockRequest;
import at.ac.uibk.keyless.Repositories.UnlockRequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class LockRequestServiceTest {

  @Autowired
  private LockRequestService lockRequestService;

  @Autowired
  private UnlockRequestRepository unlockRequestRepository;


  @Before
  public void setup() {
    unlockRequestRepository.findAll()
      .forEach(r -> unlockRequestRepository.delete(r));
  }

  /**
   * Test that only a recent unlockRequest will open a lock.
   */
  @Test
  public void testLockRequest() {
    assertThat(lockRequestService.isToUnlock(1L), is(false));

    UnlockRequest r = new UnlockRequest();
    r.setIssued(new Date(System.currentTimeMillis() - 3600 * 1000));
    r.setLockId(1L);
    unlockRequestRepository.save(r);

    assertThat(lockRequestService.isToUnlock(1L), is(false));

    lockRequestService.addNewUnlockRequest(1L);
    assertThat(lockRequestService.isToUnlock(1L), is(true));
  }
}
