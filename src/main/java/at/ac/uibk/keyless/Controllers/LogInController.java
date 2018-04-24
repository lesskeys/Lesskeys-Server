package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.LogInEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.LogInEntryRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@RestController
public class LogInController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LogInEntryRepository loginRepository;

  private static String generateToken(String deviceId) {
    String alphabet = deviceId;
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(32)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }

  private LogInEntry saveNewLogInEntry(String deviceId, long userId, String token) {
    LogInEntry toSave = new LogInEntry();
    toSave.setUserId(userId);
    toSave.setDeviceId(deviceId);
    toSave.setToken(token);
    toSave.setDate(new Date());
    loginRepository.save(toSave);
    return toSave;
  }


  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public Map<String, String> logInUser(@RequestBody Map<String, String> data) {
    Map<String, String> response = new HashMap<>();
    User loggedIn = userRepository.findFirstByEmail(data.get("username"));
    if (!(loggedIn == null)) {
      LogInEntry entry = saveNewLogInEntry(data.get("deviceId"), loggedIn.getUserId(),
        generateToken(data.get("deviceId")));
      response.put("answer", "Success");
      response.put("token", entry.getToken());
      response.put("date", entry.getDateAsString());
      return response;
    }
    response.put("answer", "Failure");
    return response;
  }

  @RequestMapping(value = "/autologin", method = RequestMethod.POST)
  public Map<String, String> autoLogInUser(@RequestBody Map<String, String> data) {
    Map<String, String> response = new HashMap<>();
    LogInEntry entry = loginRepository.findByDeviceId(data.get("deviceId"));
    if (entry == null) {
      response.put("answer", "Failure");
      return response;
    }
    if (entry.getDateAsString().equals(data.get("date"))
      && entry.getToken().equals(data.get("token"))) {
      String newToken = generateToken("deviceId");
      Date newDate = new Date();
      entry.setToken(newToken);
      entry.setDate(newDate);
      loginRepository.save(entry);
      response.put("answer", "Success");
      response.put("token", newToken);
      response.put("date", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(newDate));
      return response;
    }
    response.put("answer", "Failure");
    return response;
  }
}
