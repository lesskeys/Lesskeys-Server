package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.SystemLogRequest;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.SystemLogRequestService;
import at.ac.uibk.keyless.Services.SystemLogService;
import at.ac.uibk.keyless.Services.UserService;
import edu.emory.mathcs.backport.java.util.Collections;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
///CLOVER:OFF
@RestController
public class SystemLogController {

  @Autowired
  private SystemLogService systemLogService;

  @Autowired
  private SystemLogRequestService requestService;

  @Autowired
  private SessionService sessionService;

  @Autowired
  private UserService userService;


  @RequestMapping(value = "/log", method = RequestMethod.POST)
  public List<SystemLogEntry> getFullLog(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      User user = userService.getUserByEmail(data.get("username"));
      List<SystemLogEntry> list = new ArrayList<>(systemLogService.getEntriesForUser(user.getUserId()));
      list.sort(Comparator.comparing(SystemLogEntry::getLogTime).reversed());
      return list;
    }
    return null;
  }

  @RequestMapping(value = "/log/requests", method = RequestMethod.POST)
  public List<SystemLogRequest> getUsersRequests(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      User user = userService.getUserByEmail(data.get("username"));
      return requestService.getRequestsForUser(user);
    }
    return null;
  }

  @RequestMapping(value = "/log/request/newest", method = RequestMethod.POST)
  public SystemLogRequest getUsersMostRecentRequest(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      User user = userService.getUserByEmail(data.get("username"));
      return requestService.getRequestsForUser(user).stream()
        .max(Comparator.comparing(SystemLogRequest::getDay).reversed())
        .orElse(null);
    }
    return null;
  }

  @RequestMapping(value = "/log/accept-request", method = RequestMethod.PUT)
  public void acceptRequest(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      User user = userService.getUserByEmail(data.get("username"));
      SystemLogRequest request = requestService.getById(Long.parseLong(data.get("requestId")));
      requestService.acceptRequestForUser(request, user);
    }
  }
}
///CLOVER:ON