package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.SystemLogType;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SystemLogRequestService;
import at.ac.uibk.keyless.Services.SystemLogService;
import at.ac.uibk.keyless.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Lukas Dötlinger.
 */
///CLOVER:OFF
@RestController
public class AdminInterfaceController {

  @Autowired
  LockService lockService;

  @Autowired
  UserService userService;

  @Autowired
  SystemLogService systemLogService;

  @Autowired
  SystemLogRequestService logRequestService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private final Logger log = LoggerFactory.getLogger(this.getClass());


  @RequestMapping(value = "/ai/login", method = RequestMethod.POST)
  public Map<String, Object> adminLogin(@RequestBody Map<String, String> data) {
    Map<String, Object> toReturn = new HashMap<>();
    String username = data.get("username");
    User user = userService.getUserByEmail(username);
    String password = data.get("password");
    toReturn.put("value", (userService.hasRole(user, "Admin") &&
      passwordEncoder.matches(password, user.getPassword())) ? "true" : "false");
    if (toReturn.get("value").toString().equals("true")) {
      toReturn.put("user", user);
    }
    return toReturn;
  }

  @RequestMapping(value = "/ai/locks", method = RequestMethod.GET)
  public List<Lock> getAllLocks() {
    return lockService.getAllLocks();
  }

  @RequestMapping(value = "/ai/add-lock", method = RequestMethod.POST)
  public Map<String, String> addLock(@RequestBody Map<String, String> data) {
    Map<String, String> toReturn = new HashMap<>();
    Lock createdLock = lockService.addLock(
      Long.parseLong(data.get("userId")),
      data.get("name"),
      data.get("address"),
      data.get("code")
    );
    toReturn.put("value", createdLock != null ? "true" : "false");
    return toReturn;
  }

  @RequestMapping(value = "/ai/{lockId}/edit", method = RequestMethod.POST)
  public Map<String, String> updateLockAddress(@PathVariable(value = "lockId") long lockId,
                                @RequestBody Map<String, String> data) {
    lockService.updateAddressAndName(lockId, data.get("address"), data.get("name"));
    Map<String, String> toReturn = new HashMap<>();
    toReturn.put("value", "true");
    return toReturn;
  }

  @RequestMapping(value = "/ai/log", method = RequestMethod.POST)
  public List<SystemLogEntry> getEntries(@RequestBody Map<String, String> data) {
    List<SystemLogEntry> list = new ArrayList<>(systemLogService.getEntriesForUser(Long.parseLong(data.get("userId"))));
    list.sort(Comparator.comparing(SystemLogEntry::getLogTime).reversed());
    return list;
  }

  @RequestMapping(value = "/ai/log/request", method = RequestMethod.POST)
  public boolean requestLog(@RequestBody Map<String, String> data) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    String message = data.get("message");
    Date logDay;
    try {
      logDay = sdf.parse(data.get("date"));
    } catch (ParseException e) {
      log.error("/ai/log/request: date could not be parsed from string");
      return false;
    }

    if (data.get("type").equals("UNLOCK")) {
      logRequestService.createRequest(lockService.getLockById(1L), logDay, message, true);
    } else {
      logRequestService.createRequest(Long.parseLong(data.get("userId")), logDay, SystemLogType.SYSTEM, message, true);
      logRequestService.createRequest(Long.parseLong(data.get("userId")), logDay, SystemLogType.LOGIN, message, true);
    }
    return true;
  }
}
///CLOVER:ON