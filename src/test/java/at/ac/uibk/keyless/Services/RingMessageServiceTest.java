package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.RingMessageRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class RingMessageServiceTest {

  @Autowired
  private RingMessageService ringMessageService;

  @Autowired
  private RingMessageRepository ringMessageRepository;

  @Autowired
  private UserService userService;


  @Before
  public void setup() {
    ringMessageRepository.findAll()
      .forEach(m -> ringMessageRepository.delete(m));
  }

  @Test
  public void testRingMessageService() {
    User user = userService.getUserById(1L);
    assertThat(ringMessageService.getRingMessagesForUser(user.getEmail()).size(), is(0));

    ringMessageService.saveRingMessage("Sender", "Message", 1L);
    assertThat(ringMessageService.getRingMessagesForUser(user.getEmail()).size(), is(1));
  }
}
