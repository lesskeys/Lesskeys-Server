package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Models.UserRole;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.SystemLogService;
import at.ac.uibk.keyless.Services.UserService;
import clover.it.unimi.dsi.fastutil.Hash;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Lukas Dötlinger.
 */
///CLOVER:OFF
@RestController
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  SessionService sessionService;

  @Autowired
  LockService lockService;

  @Autowired
  SystemLogService logService;


  /**
   * Get User if User matches the valid session.
   */
  @RequestMapping(value = "/user/current", method = RequestMethod.POST)
  public User getCurrentUser(@RequestBody Map<String, String> data) {
    User user = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return user;
    }
    return null;
  }

  @RequestMapping(value = "/user/subusers", method = RequestMethod.POST)
  public List<User> getSubUsers(@RequestBody Map<String, String> data) {
    User user = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return userService.getActiveSubUsersForUser(user);
    }
    return null;
  }

  @RequestMapping(value = "/user/subuser/disable", method = RequestMethod.POST)
  public Map<String, String> disableSubUser(@RequestBody Map<String, String> data) {
    Map<String, String> toReturn = new HashMap<>();
    User user = userService.getUserByEmail(data.get("username"));
    User toDisable = userService.getUserById(Long.parseLong(data.get("toDisableId")));
    String session = data.get("session");
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))
      && (toDisable.getCreator().getUserId() == user.getUserId())) {
      userService.disableUser(toDisable);
      toReturn.put("value", "true");
      return toReturn;
    }
    toReturn.put("value", "false");
    return toReturn;
  }

  @RequestMapping(value = "/user/subuser/edit-permission", method = RequestMethod.POST)
  public void editSubUsersPermissions(@RequestBody Map<String, Object> data) {
    User user = userService.getUserByEmail(data.get("username").toString());
    String session = data.get("session").toString();
    if (sessionService.userMatchesValidSession(data.get("session").toString(), data.get("username").toString())) {
      String subUserName = data.get("subUser").toString();
      if (userService.getActiveSubUsersForUser(user).stream()
        .anyMatch(su -> su.getEmail().equals(subUserName))) {
        User subUser = userService.getUserByEmail(subUserName);
        lockService.removeUserFromLocks(subUser.getUserId());
        lockService.addUserToLocks((List<Object>) data.get("lockIds"), subUser.getUserId());
        logService.logSystemEvent(user,"User "+subUser.getUserId()+"'s permission was edited", subUser);
      }
    }
  }

  /**
   * Method used by a user to change his settings.
   */
  @RequestMapping(value = "/user/edit", method = RequestMethod.PUT)
  public void editUser(@RequestBody Map<String, Object> data) {
    User toEdit = userService.getUserByEmail(data.get("username").toString());
    String session = data.get("session").toString();
    if (sessionService.userMatchesValidSession(data.get("session").toString(), data.get("username").toString())) {
      toEdit.setEmail(Optional.ofNullable(data.get("newUsername").toString()).orElse(toEdit.getEmail()));
      toEdit.setFirstName(Optional.ofNullable(data.get("newFirstName").toString()).orElse(toEdit.getFirstName()));
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      Date newBirthday = null;
      try {
        newBirthday = sdf.parse(data.get("newBirthday").toString());
      } catch (Exception e) {}
      userService.saveUser(toEdit);
    }
  }

  /**
   * Method used by a user to change his password.
   */
  @RequestMapping(value = "/user/edit-password", method = RequestMethod.POST)
  public String editPassword(@RequestBody Map<String, String> data) {
    User toEdit = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return userService.editUsersPassword(toEdit, data.get("oldPw"), data.get("newPw1"), data.get("newPw2"));
    }
    return "Failure";
  }

  /**
   * Method to add a new User.
   */
  @RequestMapping(value = "/user/add", method = RequestMethod.POST)
  public Map<String, String> addUser(@RequestBody Map<String, Object> data) {
    Map<String, String> response = new HashMap<>();
    User operatingUser = userService.getUserByEmail(data.get("username").toString());
    if (sessionService.userMatchesValidSession(data.get("session").toString(), data.get("username").toString())) {
      User newUser = new User();
      newUser.setEmail(data.get("newUsername").toString());
      if (data.get("newPw1").equals(data.get("newPw2"))) {
        newUser.setPassword(data.get("newPw1").toString());
      } else {
        response.put("status", "Passwords don't match!");
        return response;
      }
      newUser.setFirstName(data.get("newFirstName").toString());
      newUser.setLastName(data.get("newLastName").toString());
      Set<UserRole> roles = new HashSet<>();
      roles.add(userService.getRoleForString(data.get("newRole").toString()));
      newUser.setRoles(roles);
      newUser.setCreatedAt(new Date());

      newUser.setCreator(operatingUser);
      newUser = userService.saveUser(newUser);
      lockService.addUserToLocks((List<Object>) data.get("lockIds"), newUser.getUserId());
      response.put("status", "Added new user!");
      logService.logSystemEvent(operatingUser,"User "+newUser.getUserId()+" was added", newUser);
      return response;
    }
    response.put("status", "Failure!");
    return response;
  }
}
///CLOVER:ON