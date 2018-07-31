package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class RingController {

  @Autowired
  UserService userService;


  @RequestMapping(value = "/ring/list", method = RequestMethod.GET)
  public List<User> getRingList() {
    return userService.getAllUsers();
  }
}
