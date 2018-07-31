package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.Key;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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


  private Key newKey;

  @Before
  public void addKey() {
    Key toSave = new Key();
    keyService.registerKey("test", "test", "admin@keyless.com", "test", toSave);
    newKey = keyService.getKeyById(toSave.getKeyId());
  }

  @Test
  public void testKeyRegistration() {
    long highestKeyId = keyService.getAllKeys().stream()
      .mapToLong(k -> k.getKeyId())
      .max().orElse(1L);
    assertThat(keyService.getKeyById(highestKeyId+1), is(nullValue()));
    assertThat(keyService.getKeysForUser("admin@keyless.com").stream()
      .anyMatch(k -> k.getKeyName().equals("newestKey")), is(false));

    keyService.registerKey("010", "AAAA", "admin@keyless.com", "newestKey", new Key());
    assertThat(keyService.getKeyById(highestKeyId+1), is(notNullValue()));
    assertThat(keyService.getKeysForUser("admin@keyless.com").stream()
      .anyMatch(k -> k.getKeyName().equals("newestKey")), is(true));
  }

  @Test
  public void testGetKeysForUser() {
    List<Key> adminList = keyService.getKeysForUser("admin@keyless.com");
    List<Key> modList = keyService.getKeysForUser("lukas@keyless.com");
    assertThat(modList.stream()
      .allMatch(k1 -> adminList.stream()
        .anyMatch(k2 -> k1.getKeyName().equals(k2.getKeyName()))), is(true));
    assertThat(adminList.stream()
      .allMatch(k1 -> modList.stream()
        .anyMatch(k2 -> k1.getKeyName().equals(k2.getKeyName()))), is(false));
  }

  @Test
  public void testKeyEdit() {
    Key toEdit = keyService.getKeyById(1L);
    assertThat(toEdit.getKeyName(), not(is("test")));

    Key newKey = new Key();
    newKey.setKeyName("test");
    newKey.setCustomPermission(!toEdit.isCustomPermission());
    keyService.editKey(1L, newKey, "admin@keyless.com");

    Key edited = keyService.getKeyById(1L);
    assertThat(edited.getKeyName(), is("test"));
    assertThat(edited.isCustomPermission(), not(toEdit.isCustomPermission()));
  }

  @Test
  public void testIsValid() {
    newKey.setCustomPermission(true);
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

  public void testDeleteKey() {
    assertThat(keyService.getKeyById(newKey.getKeyId()), is(notNullValue()));
    keyService.deleteKey(newKey.getKeyId(), "admin@keyless.com");
    assertThat(keyService.getKeyById(newKey.getKeyId()), is(nullValue()));
  }
}
