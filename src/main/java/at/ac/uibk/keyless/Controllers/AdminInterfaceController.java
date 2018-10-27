package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.LockService;
import at.ac.uibk.keyless.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class AdminInterfaceController {

  @Autowired
  LockService lockService;

  @Autowired
  UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;


  @RequestMapping(value = "/ai/login", method = RequestMethod.POST)
  public boolean adminLogin(@RequestBody Map<String, String> data) {
    String username = data.get("username");
    User user = userService.getUserByEmail(username);
    String password = data.get("password");
    return (userService.hasRole(user, "Admin") &&
      passwordEncoder.matches(password, user.getPassword()));
  }

  @RequestMapping(value = "/ai/locks", method = RequestMethod.GET)
  public List<Lock> getAllLocks() {
    return lockService.getAllLocks();
  }

  @RequestMapping(value = "/ai/{lockId}/address", method = RequestMethod.PUT)
  public void updateLockAddress(@PathVariable(value = "lockId") long lockId,
                                @RequestBody String newAddress) {
    lockService.updateAddress(lockId, newAddress);
  }
}
