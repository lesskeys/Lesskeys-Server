package at.ac.uibk.keyless.controllers;

import at.ac.uibk.keyless.models.Key;
import at.ac.uibk.keyless.models.KeyPermission;
import at.ac.uibk.keyless.services.KeyPermissionService;
import at.ac.uibk.keyless.services.KeyService;
import at.ac.uibk.keyless.services.SessionService;
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

  @Autowired
  SessionService sessionService;


  @RequestMapping(value = "/key/edit/permission", method = RequestMethod.PUT)
  public void editKeyPermission(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      int day = Integer.parseInt(data.get("day"));
      Key key = keyService.getKeyById(Long.parseLong(data.get("keyId")));
      keyPermissionService.editPermissionDay(day, data.get("newFrom"), data.get("newTo"), key.getPermission());
    }
  }

  @RequestMapping(value = "/key/edit/entire-permission", method = RequestMethod.PUT)
  public void editEntireKeyPermission(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      Key key = keyService.getKeyById(Long.parseLong(data.get("keyId")));
      keyPermissionService.editPermission(data, key.getPermission());
    }
  }

  @RequestMapping(value = "/key/permission", method = RequestMethod.POST)
  public KeyPermission getPermissionForKey(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      return keyService.getKeyById(Long.parseLong(data.get("keyId"))).getPermission();
    }
    return null;
  }
}
