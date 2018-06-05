package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.KeyService;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class LockController {

  @Autowired
  LockService lockService;

  @Autowired
  SessionService sessionService;

  @Autowired
  UserService userService;

  @Autowired
  KeyService keyService;


  /**
   * @return locks for a given keyId.
   */
  @RequestMapping(value = "/lock/get-for-key", method = RequestMethod.POST)
  public List<Lock> getLocksForKey(@RequestBody Map<String, String> data) {
    long keyId = Long.parseLong(data.get("keyId"));
    if (sessionService.isValidSession(data.get("session")) &&
      keyService.getKeysForUser(data.get("username")).stream()
        .anyMatch(key -> key.getKeyId() == keyId)) {
      return lockService.getLocksForKey(keyId);
    }
    return null;
  }

  /**
   * @return locks for a given userId.
   */
  @RequestMapping(value = "/lock/get-for-user", method = RequestMethod.POST)
  public List<Lock> getLocksForUser(@RequestBody Map<String, String> data) {
    User user = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    if (sessionService.isValidSession(session) && sessionService.userMatchesSession(session, user.getUserId())) {
      return lockService.getLocksForUser(user.getUserId());
    }
    return null;
  }

  @RequestMapping(value = "/lock/verify-user", method = RequestMethod.POST)
  public boolean verifyUser(@RequestBody Map<String, String> data) {
    Lock lock = lockService.getLockForIdAndCode(Long.parseLong(data.get("lockId")), data.get("code"));
    if (lock != null) {
      User user = userService.getUserByEmail(data.get("username"));
      String session = data.get("session");
      return sessionService.userMatchesSession(session, user.getUserId()) &&
        lock.getRelevantUserIds().contains(user.getUserId());
    }
    return false;
  }

  @RequestMapping(value = "/lock/verify-key", method = RequestMethod.POST)
  public boolean verifyKey(@RequestBody Map<String, String> data) {
    Lock lock = lockService.getLockForIdAndCode(Long.parseLong(data.get("lockId")), data.get("code"));
    if (lock != null) {
      User user = userService.getUserByEmail(data.get("username"));
      return keyService.isValidContent(data.get("content")) &&
        lock.getRelevantUserIds().contains(user.getUserId());
    }
    return false;
  }
}
