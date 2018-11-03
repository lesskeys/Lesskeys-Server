package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class KeyController {

  @Autowired
  KeyService keyService;

  @Autowired
  KeyPermissionService keyPermissionService;

  @Autowired
  LockService lockService;

  @Autowired
  SessionService sessionService;

  @Autowired
  SystemLogService systemLogService;

  @Autowired
  SystemLogService logService;


  @RequestMapping(value = "/key/register", method = RequestMethod.POST)
  public Map<String, String> registerKey(@RequestBody Map<String, Object> data) {
    Map<String, String> response = new HashMap<>();
    if (sessionService.isValidSession(data.get("session").toString())) {
      Key newKey = new Key();
      keyService.registerKey(data.get("aid").toString(), data.get("content").toString(),
        data.get("username").toString(), data.get("name").toString(), newKey, data.get("uid").toString());
      lockService.addKeysToLocks((List<Object>) data.get("lockIds"), newKey.getKeyId());
      response.put("status", "Successfully added key!");
      // Log event implemented in service method.
      return response;
    }
    response.put("status", "Failed to register key!");
    return response;
  }

  @RequestMapping(value = "/key/edit", method = RequestMethod.PUT)
  public void editKey(@RequestBody Map<String, Object> data) {
    if (sessionService.isValidSession(data.get("session").toString())) {
      Key newKey = new Key();
      newKey.setKeyName(data.get("newName").toString());
      newKey.setCustomPermission(Boolean.parseBoolean(data.get("isCustom").toString()));
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      Date newValidFrom = null;
      Date newValidTo = null;
      try {
        newValidFrom = sdf.parse(data.get("validFrom").toString());
        newValidTo = sdf.parse(data.get("validTo").toString());
      } catch (Exception e) {}
      newKey.setValidFrom(Optional.ofNullable(newValidFrom).orElse(newKey.getValidFrom()));
      newKey.setValidTo(Optional.ofNullable(newValidTo).orElse(newKey.getValidTo()));
      keyService.editKey(Long.parseLong(data.get("keyId").toString()), newKey, data.get("username").toString());
      lockService.removeKeyFromLocks(Long.parseLong(data.get("keyId").toString()));
      lockService.addKeysToLocks((List<Object>) data.get("lockIds"), Long.parseLong(data.get("keyId").toString()));
      // Log event implemented in service method.
    }
  }

  @RequestMapping(value = "/key/delete", method = RequestMethod.POST)
  public Map<String, String> deleteKey(@RequestBody Map<String, String> data) {
    Map<String, String> toReturn = new HashMap<>();
    if (sessionService.isValidSession(data.get("session"))) {
      keyService.deleteKey(Long.parseLong(data.get("keyId")), data.get("username"));
      toReturn.put("status", "Success");
      return toReturn;
    } else {
      toReturn.put("status", "Failure");
      return toReturn;
    }
    // Log event implemented in service method.
  }

  @RequestMapping(value = "/keys", method = RequestMethod.POST)
  public List<Key> getUsersKeys(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      return keyService.getKeysForUser(data.get("username"));
    }
    return null;
  }

  @RequestMapping(value = "/key-by-id", method = RequestMethod.POST)
  public Key getSpecificKey(@RequestBody Map<String, String> data) {
    if (sessionService.isValidSession(data.get("session"))) {
      return keyService.getKeyByIdForUser(Long.parseLong(data.get("keyId")), data.get("username"));
    }
    return null;
  }

  @RequestMapping(value = "/key/find", method = RequestMethod.POST)
  public Map<String, String> getKeyOwner(@RequestBody Map<String, Object> data) {
    Map<String, String> toReturn = new HashMap<>();
    if (sessionService.isValidSession(data.get("session").toString())) {
      Key found = keyService.getKeyByUid(data.get("uid").toString());
      if (found != null) {
        toReturn.put("firstName", found.getOwner().getFirstName());
        toReturn.put("lastName", found.getOwner().getLastName());
        return toReturn;
      }
    }
    return null;
  }
}
