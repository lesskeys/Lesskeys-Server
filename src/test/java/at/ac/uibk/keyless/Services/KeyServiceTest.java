package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.KeyMode;
import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Repositories.LockRepository;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
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
public class KeyServiceTest {

  @Autowired
  KeyService keyService;

  @Autowired
  LockService lockService;

  @Autowired
  LockRepository lockRepository;

  @Autowired
  UserService userService;


  private Key newKey;

  private final long lockId = 1L;
  private final String username = "admin@keyless.com";
  private final String keyContent = "test";

  @Before
  public void addKey() {
    Key toSave = new Key();
    toSave.setKeyName("test");
    toSave.setMode(KeyMode.CUSTOM);
    keyService.registerKey(keyContent, username, toSave);
    newKey = keyService.getKeyById(toSave.getKeyId());
  }

  @Test
  public void testKeyRegistration() {
    assertThat(keyService.getKeyByIdForUser(newKey.getKeyId(), username).getKeyName(),
      is(newKey.getKeyName()));
    assertThat(keyService.getKeyByIdForUser(newKey.getKeyId(), "test@keyless.com"),
      is(nullValue()));

    long highestKeyId = keyService.getAllKeys().stream()
      .mapToLong(Key::getKeyId)
      .max().orElse(1L);
    assertThat(keyService.getKeyById(highestKeyId+1), is(nullValue()));
    assertThat(keyService.getKeysForUser(username).stream()
      .anyMatch(k -> k.getKeyName().equals("newestKey")), is(false));

    Key newKey = new Key();
    newKey.setKeyName("newestKey");
    newKey.setMode(KeyMode.CUSTOM);
    keyService.registerKey("AAAA", username, newKey);
    assertThat(keyService.getKeyById(highestKeyId+1), is(notNullValue()));
    assertThat(keyService.getKeysForUser(username).stream()
      .anyMatch(k -> k.getKeyName().equals("newestKey")), is(true));
  }

  @Test
  public void testGetKeysForUser() {
    List<Key> adminList = keyService.getKeysForUser(username);
    List<Key> modList = keyService.getKeysForUser("lukas@keyless.com");
    assertThat(modList.stream()
      .allMatch(k1 -> adminList.stream()
        .anyMatch(k2 -> k1.getKeyName().equals(k2.getKeyName()))), is(false));
    assertThat(adminList.stream()
      .allMatch(k1 -> modList.stream()
        .anyMatch(k2 -> k1.getKeyName().equals(k2.getKeyName()))), is(false));
  }

  @Test
  public void testKeyEdit() {
    Key toEdit = keyService.getKeyById(1L);
    assertThat(toEdit.getKeyName(), not(is("test")));

    toEdit.setKeyName("test");
    toEdit.setMode(KeyMode.CUSTOM);
    keyService.editKey(toEdit, userService.getUserByEmail(username));

    Key edited = keyService.getKeyById(1L);
    assertThat(edited.getKeyName(), is("test"));
  }

  @Test
  public void testIsValid() {
    newKey.setMode(KeyMode.CUSTOM);
    Calendar c = Calendar.getInstance();
    c.add(Calendar.MONTH, -1);
    newKey.setValidFrom(c.getTime());
    c.add(Calendar.MONTH, 2);
    newKey.setValidTo(c.getTime());

    assertThat(keyService.isValid(newKey), is(true));

    c.setTime(new Date());
    c.add(Calendar.HOUR, -24);
    newKey.setValidTo(c.getTime());

    assertThat(keyService.isValid(newKey), is(false));
  }

  @Test
  public void testDeleteKey() {
    assertThat(keyService.getKeyById(newKey.getKeyId()), is(notNullValue()));
    keyService.deleteKey(newKey.getKeyId(), username);
    assertThat(keyService.getKeyById(newKey.getKeyId()), is(nullValue()));
  }
}
