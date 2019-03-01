package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.LockRequestService;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
///CLOVER:OFF
@RestController
public class LockRequestController {

  @Autowired
  LockRequestService lockRequestService;

  @Autowired
  SessionService sessionService;

  @Autowired
  LockService lockService;

  @Autowired
  UserService userService;


  @RequestMapping(value = "/lock/request", method = RequestMethod.PUT)
  public void sendMessage(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session").toString())) {
      lockRequestService.sendMessageToLock(Long.parseLong(data.get("lockId")), data.get("message"));
    }
  }

  @RequestMapping(value = "/lock/check-unlock", method = RequestMethod.POST)
  public boolean checkUnlock(@RequestBody Map<String, String> data) {
    Long lockId = Long.parseLong(data.get("lockId"));
    Lock lock = lockService.getLockForIdAndCode(lockId, data.get("code"));
    if (lock != null) {
      return lockRequestService.isToUnlock(lockId);
    }
    return false;
  }

  @RequestMapping(value = "/lock/remote-unlock", method = RequestMethod.PUT)
  public void issueRemoteUnlock(@RequestBody Map<String, String> data) {
    User user = userService.getUserByEmail(data.get("username"));
    Lock lock = lockService.getLockById(Long.parseLong(data.get("lockId")));
    if (sessionService.isValidSession(data.get("session")) &&
      sessionService.userMatchesSession(data.get("session"), user.getUserId())) {
      if (lock.getRelevantUserIds().contains(user.getUserId())) {
        lockRequestService.addNewUnlockRequest(lock.getLockId());
        lockRequestService.logRemoteUnlock(lock, user);
        lockRequestService.sendMessageToLock(lock.getLockId(), "unlock");
      }
    }
  }
}
///CLOVER:ON