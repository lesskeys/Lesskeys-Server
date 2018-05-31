package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  SessionService sessionService;


  @RequestMapping(value = "/user/edit", method = RequestMethod.PUT)
  public void editUser(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      //TODO: Only get user if session is valid for it.
      User toEdit = userService.getUserByEmail(data.get("username"));
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

  @RequestMapping(value = "/user/edit-password", method = RequestMethod.POST)
  public String editPassword(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      //TODO: Only get user if session is valid for it.
      User toEdit = userService.getUserByEmail(data.get("username"));
      return userService.editUsersPassword(toEdit, data.get("oldPw"), data.get("newPw1"), data.get("newPw2"));
    }
    return "Failure";
  }
}
