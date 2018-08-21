package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  @Autowired
  SystemLogService systemLogService;


  /**
   * @return locks for a given keyId.
   */
  @RequestMapping(value = "/lock/get-for-key", method = RequestMethod.POST)
  public List<Lock> getLocksForKey(@RequestBody Map<String, String> data) {
    long keyId = Long.parseLong(data.get("keyId"));
    if (sessionService.isValidSession(data.get("session")) &&
      keyService.getKeysForUser(data.get("username")).stream()
        .anyMatch(key -> key.getKeyId() == keyId)) {
      systemLogService.logEvent("requested lock for key", "User: "
          +userService.getUserByEmail(data.get("username")).getUserId(), "Key: "+keyId);
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
      systemLogService.logEvent("requested lock for user", "User: "
        +userService.getUserByEmail(data.get("username")).getUserId(), "self");
      return lockService.getLocksForUser(user.getUserId());
    }
    return null;
  }

  /**
   * @return locks for a given userId if the requesting user is it's superior.
   */
  @RequestMapping(value = "/lock/get-for-subuser", method = RequestMethod.POST)
  public List<Lock> getLocksForSubUser(@RequestBody Map<String, String> data) {
    User user = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    Long subUserId = Long.parseLong(data.get("subUserId"));
    if (sessionService.isValidSession(session) && sessionService.userMatchesSession(session, user.getUserId())
      && user.getSubUsers().stream()
        .anyMatch(u -> u.getUserId() == subUserId)) {
      systemLogService.logEvent("requested lock for user", "User: "
        +userService.getUserByEmail(data.get("username")).getUserId(), "User: "+subUserId);
      return lockService.getLocksForUser(subUserId);
    }
    return null;
  }

  @RequestMapping(value = "/lock/verify-user", method = RequestMethod.POST)
  public boolean verifyUser(@RequestBody Map<String, String> data) {
    Lock lock = lockService.getLockForIdAndCode(Long.parseLong(data.get("lockId")), data.get("code"));
    if (lock != null) {
      User user = userService.getUserByEmail(data.get("username"));
      String session = data.get("session");
      systemLogService.logEvent("lock tried to verify user", "Lock: "+lock.getLockId(),
        "User: "+user.getUserId());
      return sessionService.userMatchesSession(session, user.getUserId()) &&
        lock.getRelevantUserIds().contains(user.getUserId());
    }
    return false;
  }

  @RequestMapping(value = "/lock/verify-key", method = RequestMethod.POST)
  public boolean verifyKey(@RequestBody Map<String, String> data) {
    Lock lock = lockService.getLockForIdAndCode(Long.parseLong(data.get("lockId")), data.get("code"));
    if (lock != null) {
      return keyService.isValidContent(data.get("content"), lock);
    }
    return false;
  }

  @RequestMapping(value = "/lock/keys", method = RequestMethod.POST)
  public List<Map<String, Object>> getKeysForLock(@RequestBody Map<String, String> data) {
    Lock lock = lockService.getLockForIdAndCode(Long.parseLong(data.get("lockId")), data.get("code"));
    ObjectMapper mapper = new ObjectMapper();
    if (lock != null) {
      return lock.getRelevantKeys().stream()
        .map(k -> {
          Map<String, Object> asMap = mapper.convertValue(k, new TypeReference<Map<String, Object>>() {});
          asMap.put("content", k.getContent());
          return asMap;
        })
        .collect(Collectors.toList());
    }
    return null;
  }

  @RequestMapping(value = "/lock/sessions", method = RequestMethod.POST)
  public List<Map<String, Object>> getSessionsForLock(@RequestBody Map<String, String> data) {
    Lock lock = lockService.getLockForIdAndCode(Long.parseLong(data.get("lockId")), data.get("code"));
    ObjectMapper mapper = new ObjectMapper();
    if (lock != null) {
      return lock.getRelevantUsers().stream()
        .filter(u -> sessionService.getByUserId(u.getUserId()) != null)
        .map(u -> {
          Map<String, Object> asMap = mapper.convertValue(sessionService.getByUserId(u.getUserId()),
            new TypeReference<Map<String, Object>>() {});
          asMap.put("username", u.getEmail());
          return asMap;
        })
        .collect(Collectors.toList());
    }
    return null;
  }
}
