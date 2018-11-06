package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.SystemLogEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SystemLogServiceTest {

  @Autowired
  SystemLogService systemLogService;

  @Before
  public void addLog() {
    systemLogService.logEvent("test", 1L);
  }

  @Test
  public void testLogService() {
    List<SystemLogEntry> log = systemLogService.getAll();
    assertThat(log, is(notNullValue()));

    assertThat(log.stream()
      .anyMatch(e -> e.getEvent().endsWith("test")), is(true));
  }
}
