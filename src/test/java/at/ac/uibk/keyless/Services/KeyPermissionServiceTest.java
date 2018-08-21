package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.KeyPermission;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Lukas Dötlinger.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class KeyPermissionServiceTest {

  @Autowired
  KeyPermissionService permissionService;

  @Autowired
  KeyService keyService;


  private KeyPermission permission;
  private Key test1 = new Key();
  private Key test2 = new Key();

  @Before
  public void init() {
    keyService.registerKey("test1", "test1", "admin@keyless.com", "test1", test1);
    keyService.registerKey("test2", "test2", "admin@keyless.com", "test2", test2);
    test1 = keyService.getKeyById(test1.getKeyId());
    test2 = keyService.getKeyById(test2.getKeyId());
    permission = permissionService.savePermission(new KeyPermission(test1));
  }

  @Test
  public void testGetTimeForDay() {
    assertThat(permissionService.getFromTimeForDay(Calendar.MONDAY, permission), is(permission.getMondayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.TUESDAY, permission), is(permission.getTuesdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.WEDNESDAY, permission), is(permission.getWednesdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.THURSDAY, permission), is(permission.getThursdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.FRIDAY, permission), is(permission.getFridayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.SATURDAY, permission), is(permission.getSaturdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.SUNDAY, permission), is(permission.getSundayFrom()));

    assertThat(permissionService.getToTimeForDay(Calendar.MONDAY, permission), is(permission.getMondayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.TUESDAY, permission), is(permission.getTuesdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.WEDNESDAY, permission), is(permission.getWednesdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.THURSDAY, permission), is(permission.getThursdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.FRIDAY, permission), is(permission.getFridayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.SATURDAY, permission), is(permission.getSaturdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.SUNDAY, permission), is(permission.getSundayTo()));
  }

  @Test
  public void testEditTime() {
    long newPermissionId = permissionService.savePermission(new KeyPermission(test2)).getPermissionId();

    permissionService.editPermissionDay(Calendar.MONDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));
    permissionService.editPermissionDay(Calendar.TUESDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));
    permissionService.editPermissionDay(Calendar.WEDNESDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));
    permissionService.editPermissionDay(Calendar.THURSDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));
    permissionService.editPermissionDay(Calendar.FRIDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));
    permissionService.editPermissionDay(Calendar.SATURDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));
    permissionService.editPermissionDay(Calendar.SUNDAY, "10:00", "20:00", permissionService.getKeyPermissionById(newPermissionId));

    KeyPermission newPermission = permissionService.getKeyPermissionById(newPermissionId);
    ObjectMapper om = new ObjectMapper();
    Map<String, String> map = om.convertValue(newPermission, new TypeReference<Map<String, String>>() {});
    permissionService.editPermission(map, permission);

    assertThat(permissionService.getFromTimeForDay(Calendar.MONDAY, permission), is(newPermission.getMondayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.TUESDAY, permission), is(newPermission.getTuesdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.WEDNESDAY, permission), is(newPermission.getWednesdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.THURSDAY, permission), is(newPermission.getThursdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.FRIDAY, permission), is(newPermission.getFridayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.SATURDAY, permission), is(newPermission.getSaturdayFrom()));
    assertThat(permissionService.getFromTimeForDay(Calendar.SUNDAY, permission), is(newPermission.getSundayFrom()));

    assertThat(permissionService.getToTimeForDay(Calendar.MONDAY, permission), is(newPermission.getMondayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.TUESDAY, permission), is(newPermission.getTuesdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.WEDNESDAY, permission), is(newPermission.getWednesdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.THURSDAY, permission), is(newPermission.getThursdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.FRIDAY, permission), is(newPermission.getFridayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.SATURDAY, permission), is(newPermission.getSaturdayTo()));
    assertThat(permissionService.getToTimeForDay(Calendar.SUNDAY, permission), is(newPermission.getSundayTo()));
  }
}
