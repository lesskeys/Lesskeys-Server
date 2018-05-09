package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.LogInEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LogInServiceTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  LogInService logInService;

  @Before
  public void addEntry() {
    User user = userRepository.findFirstByEmail("admin@keyless.com");
    LogInEntry entry = new LogInEntry();
    entry.setDate(new Date());
    entry.setToken("token");
    entry.setDeviceId("device");
    entry.setUserId(user.getUserId());
    logInService.saveEntry(entry);
  }

  @Test
  public void testNewEntry() {
    User user = userRepository.findFirstByEmail("admin@keyless.com");
    LogInEntry newEntry = logInService.getForUsername(user.getEmail());
    assertThat(newEntry, is(notNullValue()));
    assertThat(newEntry.getToken(), is("token"));
    assertThat(newEntry.getDeviceId(), is("device"));

    logInService.updateLogInEntry("newDevice", user.getUserId(), "newToken");
    newEntry = logInService.getForUsername(user.getEmail());
    assertThat(newEntry.getDeviceId(), is("newDevice"));
    assertThat(newEntry.getToken(), is("newToken"));

    assertThat(logInService.getNewestEntryForDevice("anotherDevice"), is(nullValue()));
    logInService.updateLogInEntry("anotherDevice", userRepository.findAll().size()+1,
      "anotherToken");
    assertThat(logInService.getNewestEntryForDevice("anotherDevice"), is(notNullValue()));

    assertThat(logInService.getNewestEntryForDevice("newDevice").getToken(), is("newToken"));
    assertThat(logInService.getNewestEntryForDevice("device"), is(nullValue()));
  }

}
