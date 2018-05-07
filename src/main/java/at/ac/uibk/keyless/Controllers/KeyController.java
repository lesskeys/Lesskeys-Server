package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Services.KeyService;
import at.ac.uibk.keyless.Services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class KeyController {

  @Autowired
  KeyService keyService;

  @Autowired
  SessionService sessionService;

  @RequestMapping(value = "/key/register", method = RequestMethod.POST)
  public Map<String, String> registerKey(@RequestBody Map<String, String> data) {
    Map<String, String> response = new HashMap<>();
    if (sessionService.isValidSession((String) data.get("session"))) {
      keyService.saveKey(data.get("aid"), data.get("content"), data.get("username"));
      response.put("status", "Successfully added key.");
      return response;
    }
    response.put("status", "Failed to register key.");
    return response;
  }
}
