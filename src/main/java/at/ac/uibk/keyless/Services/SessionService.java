package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Session;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SessionService {

  @Autowired
  private SessionRepository sessionRepository;


  private String generateSessionToken() {
    String alphabet = "0123456789abcdefghijklmnopqrstuvwxyz";
    return ThreadLocalRandom.current()
      .ints(0, alphabet.length())
      .limit(64)
      .mapToObj(i -> alphabet.charAt(i))
      .map(Object::toString)
      .collect(Collectors.joining());
  }

  public String initSession(User user) {
    Session s = sessionRepository.findByUserId(user.getUserId());
    if (s == null) {
      s = new Session();
      s.setUserId(user.getUserId());
    }
    s.setSessionToken(generateSessionToken());
    s.setLastAction(new Date());
    sessionRepository.save(s);
    return s.getSessionToken();
  }
}
