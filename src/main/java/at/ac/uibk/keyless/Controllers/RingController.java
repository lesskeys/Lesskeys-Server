package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Services.RingMessageService;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.UserService;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class RingController {

  @Autowired
  UserService userService;

  @Autowired
  SessionService sessionService;

  @Autowired
  RingMessageService messageService;


  private String authCode = "";

  @RequestMapping(value = "/ring/auth/gen", method = RequestMethod.GET)
  public String getAuthCode() {
    synchronized (this) {
      this.authCode = generateAuthCode();
    }
    return authCode;
  }

  @RequestMapping(value = "/ring/auth/verify", method = RequestMethod.POST)
  public boolean verifyAuthCode(@RequestBody Map<String, String> data) {
    return authCode.equals(data.get("authCode"));
  }

  @RequestMapping(value = "/ring/list", method = RequestMethod.GET)
  public List<User> getRingList() {
    return userService.getAllUsers();
  }

  @RequestMapping(value = "/ring/send", method = RequestMethod.PUT)
  public void sendRingMessage(@RequestBody Map<String, String> data) {
    User receiver = userService.getUserById(Long.parseLong(data.get("userId")));
    String message = data.get("message");
    String sender = data.get("sender");

    String registrationToken;
    try {
      registrationToken = sessionService.getByUserId(receiver.getUserId()).getFireBaseToken();
    } catch (NullPointerException e) { return; }
    if (registrationToken == null) {
      return;
    }

    Message toSend = Message.builder()
      .setToken(registrationToken)
      .setAndroidConfig(AndroidConfig.builder()
        .setPriority(AndroidConfig.Priority.HIGH)
        .setNotification(AndroidNotification.builder()
          .setTitle("Klingel von "+sender)
          .setBody(message)
          .setIcon("stock_ticker_update")
          .build())
        .build())
      .build();

    try {
      FirebaseMessaging.getInstance().send(toSend);
      messageService.saveRingMessage(sender, message, receiver.getUserId());
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }

  private String generateAuthCode() {
    String alphabet = "0123456789abcdefghijklmnopqrstuvwxyz";
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(6)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }
}
