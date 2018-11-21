package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SystemLogService;
import at.ac.uibk.keyless.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class AdminInterfaceController {

  @Autowired
  LockService lockService;

  @Autowired
  UserService userService;

  @Autowired
  SystemLogService systemLogService;

  @Autowired
  private PasswordEncoder passwordEncoder;


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
}
