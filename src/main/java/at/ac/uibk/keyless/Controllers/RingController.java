package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.User;
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

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class RingController {

  @Autowired
  UserService userService;

  @Autowired
  SessionService sessionService;


  @RequestMapping(value = "/ring/list", method = RequestMethod.GET)
  public List<User> getRingList() {
    return userService.getAllUsers();
  }

  @RequestMapping(value = "/ring/send", method = RequestMethod.PUT)
  public void sendRingMessage(@RequestBody Map<String, String> data) {
    User receiver = userService.getUserById(Long.parseLong(data.get("userId")));
    String message = data.get("message");

    String registrationToken = sessionService.getByUserId(receiver.getUserId()).getFireBaseToken();
    if (registrationToken == null) {
      return;
    }
    Message toSend = Message.builder()
      .setToken(registrationToken)
      .setAndroidConfig(AndroidConfig.builder()
        .setNotification(AndroidNotification.builder()
          .setTitle("Klingel")
          .setBody(message)
          .setIcon("stock_ticker_update")
          .build())
        .build())
      .build();

    try {
      FirebaseMessaging.getInstance().send(toSend);
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }
}
