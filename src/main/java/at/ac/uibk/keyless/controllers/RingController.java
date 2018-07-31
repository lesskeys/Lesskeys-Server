package at.ac.uibk.keyless.controllers;

import at.ac.uibk.keyless.models.User;
import at.ac.uibk.keyless.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.faces.bean.ApplicationScoped;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Controller
@ApplicationScoped
public class RingController {

  @Autowired
  UserService userService;

  @RequestMapping(value = "/ring", method = RequestMethod.GET)
  public String ringList() {
    return "/ring.xhtml";
  }

  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  public String test() {
    return "Mieterliste";
  }
}
