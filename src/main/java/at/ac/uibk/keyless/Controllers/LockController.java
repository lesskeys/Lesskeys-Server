package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SessionService;
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


  /**
   * @return locks for a given key id.
   * TODO: Verify that a user can only retrieve the data for his keys.
   */
  @RequestMapping(value = "/lock/get-for-key", method = RequestMethod.POST)
  public List<Lock> getLocksForKey(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      return lockService.getLocksForKey(Long.parseLong(data.get("keyId")));
    }
    return null;
  }
}
