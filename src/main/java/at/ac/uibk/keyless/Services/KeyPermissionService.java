package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.KeyPermission;
import at.ac.uibk.keyless.Repositories.KeyPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class KeyPermissionService {

  @Autowired
  KeyPermissionRepository keyPermissionRepository;


  /**
   * @return lower limit of time for which permission is granted for a day
   */
  public String getFromTimeForDay(int day, KeyPermission permission) {
    switch (day) {
      case Calendar.MONDAY:    return permission.getMondayFrom();
      case Calendar.TUESDAY:   return permission.getTuesdayFrom();
      case Calendar.WEDNESDAY: return permission.getWednesdayFrom();
      case Calendar.THURSDAY:  return permission.getThursdayFrom();
      case Calendar.FRIDAY:    return permission.getFridayFrom();
      case Calendar.SATURDAY:  return permission.getSaturdayFrom();
      case Calendar.SUNDAY:    return permission.getSundayFrom();
      default:                 return "00:00";
    }
  }

  /**
   * @return upper limit of time for which permission is granted for a day
   */
  public String getToTimeForDay(int day, KeyPermission permission) {
    switch (day) {
      case Calendar.MONDAY:    return permission.getMondayTo();
      case Calendar.TUESDAY:   return permission.getTuesdayTo();
      case Calendar.WEDNESDAY: return permission.getWednesdayTo();
      case Calendar.THURSDAY:  return permission.getThursdayTo();
      case Calendar.FRIDAY:    return permission.getFridayTo();
      case Calendar.SATURDAY:  return permission.getSaturdayTo();
      case Calendar.SUNDAY:    return permission.getSundayTo();
      default:                 return "24:00";
    }
  }

  /**
   * @param permission
   * @return true if permission given for the current time, false otherwise
   */
  public boolean isValid(KeyPermission permission) {
    if (!permission.getKey().isCustomPermission()) {
      return true;
    }
    Date current = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(current);
    int currentDay = cal.get(Calendar.DAY_OF_WEEK);
    int from = Integer.parseInt(getFromTimeForDay(currentDay, permission).replaceAll("[^0-9]", ""));
    int to = Integer.parseInt(getToTimeForDay(currentDay, permission).replaceAll("[^0-9]", ""));
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    int currentTime = Integer.parseInt(dateFormat.format(current).replaceAll("[^0-9]", ""));
    if (from <= currentTime && currentTime <= to) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method to set new permission-times for a given day.
   * TODO: Check if user is allowed to do that!
   */
  public void editPermissionDay(int day, String newFrom, String newTo, KeyPermission permission) {
    switch (day) {
      case Calendar.MONDAY:
        permission.setMondayFrom(newFrom);
        permission.setMondayTo(newTo);
        break;
      case Calendar.TUESDAY:
        permission.setTuesdayFrom(newFrom);
        permission.setTuesdayTo(newTo);
        break;
      case Calendar.WEDNESDAY:
        permission.setWednesdayFrom(newFrom);
        permission.setWednesdayTo(newTo);
        break;
      case Calendar.THURSDAY:
        permission.setThursdayFrom(newFrom);
        permission.setThursdayTo(newTo);
        break;
      case Calendar.FRIDAY:
        permission.setFridayFrom(newFrom);
        permission.setFridayTo(newTo);
        break;
      case Calendar.SATURDAY:
        permission.setSaturdayFrom(newFrom);
        permission.setSaturdayTo(newTo);
        break;
      case Calendar.SUNDAY:
        permission.setSundayFrom(newFrom);
        permission.setSundayTo(newTo);
        break;
    }
    keyPermissionRepository.save(permission);
  }
}