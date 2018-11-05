package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Services.LockRequestService;
import at.ac.uibk.keyless.Services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class LockRequestController {

  @Autowired
  LockRequestService lockRequestService;

  @Autowired
  SessionService sessionService;


  @RequestMapping(value = "/lock/request", method = RequestMethod.PUT)
  public void sendMessage(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session").toString())) {
      lockRequestService.sendMessageToLock(Long.parseLong(data.get("lockId")), data.get("message"));
    }
  }
}
