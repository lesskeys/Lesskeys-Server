package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Services.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class AdminInterfaceController {

  @Autowired
  LockService lockService;


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
