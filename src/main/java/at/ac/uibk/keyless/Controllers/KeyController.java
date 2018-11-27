package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.KeyMode;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.*;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class KeyController {

  @Autowired
  KeyService keyService;

  @Autowired
  UserService userService;

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

  @Autowired
  RingMessageService messageService;

  private final Logger SpringConsoleLogger = LoggerFactory.getLogger(this.getClass());


  @RequestMapping(value = "/key/register", method = RequestMethod.POST)
  public Map<String, String> registerKey(@RequestBody Map<String, Object> data) {
    Map<String, String> response = new HashMap<>();
    if (sessionService.userMatchesValidSession(data.get("session").toString(), data.get("username").toString())) {
      Key newKey = new Key();
      newKey.setKeyName(data.get("name").toString());
      newKey.setAid(data.get("aid").toString());
      newKey.setUid(data.get("uid").toString());
      newKey.setMode(KeyMode.valueOf(data.get("mode").toString()));
      keyService.registerKey(data.get("content").toString(), data.get("username").toString(), newKey);
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
    if (sessionService.userMatchesValidSession(data.get("session").toString(), data.get("username").toString())) {
      User user = userService.getUserByEmail(data.get("username").toString());
      Key key = keyService.getKeyById(Long.parseLong(data.get("keyId").toString()));

      // check if the user is the owner of the key
      if (!(user.getKeys().stream()
        .filter(k -> k.getKeyId() == key.getKeyId())
        .collect(Collectors.toList()).size() > 0))
      { return; }


      key.setKeyName(Optional.ofNullable(data.get("newName")).orElse(key.getKeyName()).toString());
      key.setMode(KeyMode.valueOf(Optional.ofNullable(data.get("newMode")).orElse(key.getMode()).toString()));

      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      Date newValidFrom = null;
      Date newValidTo = null;
      try {
        newValidFrom = sdf.parse(data.get("validFrom").toString());
        newValidTo = sdf.parse(data.get("validTo").toString());
      } catch (Exception e) {}
      key.setValidFrom(Optional.ofNullable(newValidFrom).orElse(key.getValidFrom()));
      key.setValidTo(Optional.ofNullable(newValidTo).orElse(key.getValidTo()));

      keyService.editKey(key, user);
      lockService.removeKeyFromLocks(Long.parseLong(data.get("keyId").toString()));
      lockService.addKeysToLocks((List<Object>) data.get("lockIds"), Long.parseLong(data.get("keyId").toString()));
      // Log event implemented in service method.
    }
  }

  @RequestMapping(value = "/key/delete", method = RequestMethod.POST)
  public Map<String, String> deleteKey(@RequestBody Map<String, String> data) {
    Map<String, String> toReturn = new HashMap<>();
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
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
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return keyService.getKeysForUser(data.get("username"));
    }
    return null;
  }

  @RequestMapping(value = "/key-by-id", method = RequestMethod.POST)
  public Key getSpecificKey(@RequestBody Map<String, String> data) {
    if (sessionService.userMatchesValidSession(data.get("session"), data.get("username"))) {
      return keyService.getKeyByIdForUser(Long.parseLong(data.get("keyId")), data.get("username"));
    }
    return null;
  }

  @RequestMapping(value = "/key/find", method = RequestMethod.POST)
  public Map<String, String> getKeyOwner(@RequestBody Map<String, Object> data) {
    Map<String, String> toReturn = new HashMap<>();
    if (sessionService.userMatchesValidSession(data.get("session").toString(), data.get("username").toString())) {
      Key found = keyService.getKeyByUid(data.get("uid").toString());
      if (found != null) {
        User finder = userService.getUserByEmail(data.get("username").toString());

        // If the finder is the owner
        if (finder.getUserId() == found.getOwner().getUserId()) {
          toReturn.put("firstName", found.getOwner().getFirstName() + " " + found.getOwner().getLastName());
          toReturn.put("lastName", "Schlüssel " + found.getKeyName());
          return toReturn;
        }

        keyService.deactivateKey(found);
        toReturn.put("firstName", found.getOwner().getFirstName());
        toReturn.put("lastName", found.getOwner().getLastName());

        // Send notification to key owner
        String registrationToken;
        try {
          registrationToken = sessionService.getByUserId(found.getOwner().getUserId()).getFireBaseToken();
        } catch (NullPointerException e) {
          SpringConsoleLogger.error("/key/find: Owner wos not notified");
          return toReturn;
        }
        if (registrationToken == null) {
          SpringConsoleLogger.error("/key/find: Owner wos not notified");
          return toReturn;
        }
        String message = "Schlüssel "+found.getKeyName()+" wurde von "+finder.getFirstName()+" "+finder.getLastName()+" gefunden!";

        Message toSend = Message.builder()
          .setToken(registrationToken)
          .setAndroidConfig(AndroidConfig.builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder()
              .setTitle("Lesskeys Systemnachricht")
              .setBody(message)
              .setIcon("stock_ticker_update")
              .build())
            .build())
          .build();

        try {
          FirebaseMessaging.getInstance().send(toSend);
          messageService.saveRingMessage("System", message, found.getOwner().getUserId());
        } catch (FirebaseMessagingException e) {
          SpringConsoleLogger.error("/key/find: Owner wos not notified");
        }

        return toReturn;
      }
    }
    return null;
  }
}
