package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SystemLogController {

  @Autowired
  private SystemLogService systemLogService;

  @Autowired
  private SessionService sessionService;


  @RequestMapping(value = "/full-log", method = RequestMethod.POST)
  public List<SystemLogEntry> getFullLog(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      return systemLogService.getAll();
    }
    return null;
  }
}
