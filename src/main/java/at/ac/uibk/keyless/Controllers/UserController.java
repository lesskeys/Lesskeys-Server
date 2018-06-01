package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Models.UserRole;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.UserService;
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
@RestController
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  SessionService sessionService;


  /**
   * Get User if User matches the valid session.
   */
  @RequestMapping(value = "/user/current", method = RequestMethod.POST)
  public User getCurrentUser(@RequestBody Map<String, String> data) {
    User user = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    if (sessionService.isValidSession(session) && sessionService.userMatchesSession(session, user.getUserId())) {
      return user;
    }
    return null;
  }

  /**
   * Method used by a user to change his settings.
   */
  @RequestMapping(value = "/user/edit", method = RequestMethod.PUT)
  public void editUser(@RequestBody Map<String, String> data) {
    User toEdit = userService.getUserByEmail(data.get("username"));
    String session = data.get("session");
    if (sessionService.isValidSession(session) && sessionService.userMatchesSession(session, toEdit.getUserId())) {
      toEdit.setEmail(Optional.ofNullable(data.get("newUsername")).orElse(toEdit.getEmail()));
      toEdit.setFirstName(Optional.ofNullable(data.get("newFirstName")).orElse(toEdit.getFirstName()));
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      Date newBirthday = null;
      try {
        newBirthday = sdf.parse(data.get("newBirthday"));
      } catch (Exception e) {}
      toEdit.setBirthday(Optional.ofNullable(newBirthday).orElse(toEdit.getBirthday()));
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
    if (sessionService.isValidSession(session) && sessionService.userMatchesSession(session, toEdit.getUserId())) {
      return userService.editUsersPassword(toEdit, data.get("oldPw"), data.get("newPw1"), data.get("newPw2"));
    }
    return "Failure";
  }

  /**
   * Method to add a new User.
   * TODO: refactor this.
   */
  @RequestMapping(value = "/user/add", method = RequestMethod.POST)
  public Map<String, String> addUser(@RequestBody Map<String, String> data) {
    Map<String, String> response = new HashMap<>();
    User operatingUser = userService.getUserByEmail(data.get("username"));
    if (sessionService.isValidSession(data.get("session"))) {
      User newUser = new User();
      newUser.setEmail(data.get("newUsername"));
      if (data.get("newPw1").equals(data.get("newPw2"))) {
        newUser.setPassword(data.get("newPw1"));
      } else {
        response.put("status", "Passwords don't match!");
        return response;
      }
      newUser.setFirstName(data.get("newFirstName"));
      Set<UserRole> roles = new HashSet<>();
      roles.add(userService.getRoleForString(data.get("newRole")));
      newUser.setRoles(roles);
      newUser.setCreatedAt(new Date());

      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      try {
        newUser.setBirthday(sdf.parse(data.get("newBirthday")));
      } catch (Exception e) {}
      userService.saveUser(newUser);

      operatingUser.getSubUsers().add(newUser.getUserId());
      userService.saveUser(operatingUser);

      response.put("status", "Added new user!");
      return response;
    }
    response.put("status", "Failure!");
    return response;
  }
}
