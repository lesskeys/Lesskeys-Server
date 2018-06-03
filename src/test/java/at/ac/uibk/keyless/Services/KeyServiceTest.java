package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class KeyServiceTest {

  @Autowired
  KeyService keyService;

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
    keyService.editKey(1L, newKey);

    Key edited = keyService.getKeyById(1L);
    assertThat(edited.getKeyName(), is("test"));
    assertThat(edited.isCustomPermission(), not(toEdit.isCustomPermission()));
  }
}
