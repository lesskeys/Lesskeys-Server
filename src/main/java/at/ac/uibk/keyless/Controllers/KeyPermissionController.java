package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Services.KeyPermissionService;
import at.ac.uibk.keyless.Services.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class KeyPermissionController {

  @Autowired
  KeyPermissionService keyPermissionService;

  @Autowired
  KeyService keyService;


  @RequestMapping(value = "/key/validation", method = RequestMethod.POST)
  public boolean isValidKey(@RequestBody Map<String, String> data) {
    Key key = keyService.getKeyById(Long.parseLong(data.get("keyId")));
    return key != null;
  }
}
