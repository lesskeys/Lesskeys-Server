package at.ac.uibk.keyless.Controllers;

import at.ac.uibk.keyless.Models.LogInEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.LogInEntryRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import at.ac.uibk.keyless.Services.LogInService;
import at.ac.uibk.keyless.Services.SessionService;
import at.ac.uibk.keyless.Services.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private LogInService logInService;

  @Autowired
  private SystemLogService systemLogService;

  @Autowired
  private SessionService sessionService;

  @Autowired
  private PasswordEncoder passwordEncoder;


  private static String generateToken(String deviceId) {
    String alphabet = deviceId;
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(32)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public Map<String, String> logInUser(@RequestBody Map<String, String> data) {
    Map<String, String> response = new HashMap<>();
    User loggedIn = userRepository.findFirstByEmail(data.get("username"));
    if (!(loggedIn == null)) {
      if (data.get("username").equals(loggedIn.getEmail()) &&
        passwordEncoder.matches(data.get("password"), loggedIn.getPassword())) {
        LogInEntry entry = logInService.updateLogInEntry(data.get("deviceId"), loggedIn.getUserId(),
          generateToken(data.get("deviceId")));
        response.put("answer", "Success");
        response.put("token", entry.getToken());
        response.put("date", entry.getDateAsString());
        response.put("session", sessionService.initSession(loggedIn, data.get("firebaseToken")));
        systemLogService.logLoginEvent(loggedIn, "Logged in automatically");
        return response;
      }
    }
    response.put("answer", "Failure");
    return response;
  }

  @RequestMapping(value = "/autologin", method = RequestMethod.POST)
  public Map<String, String> autoLogInUser(@RequestBody Map<String, String> data) {
    Map<String, String> response = new HashMap<>();
    LogInEntry entry = logInService.getNewestEntryForDevice(data.get("deviceId"));
    if (!(entry == null)) {
      if (entry.getDateAsString().equals(data.get("date")) && entry.getToken().equals(data.get("token"))) {
        String newToken = generateToken(data.get("deviceId"));
        Date newDate = new Date();
        entry.setToken(newToken);
        entry.setDate(newDate);
        logInService.saveEntry(entry);
        response.put("answer", "Success");
        response.put("token", newToken);
        response.put("date", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(newDate));
        response.put("session", sessionService.initSession(userRepository.findByUserId(entry.getUserId()),
          data.get("firebaseToken")));
        systemLogService.logLoginEvent(userRepository.findByUserId(entry.getUserId()), "Logged in automatically");
        return response;
      }
    }
    response.put("answer", "Failure");
    return response;
  }
}
