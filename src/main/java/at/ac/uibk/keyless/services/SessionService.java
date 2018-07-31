package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.Session;
import at.ac.uibk.keyless.models.User;
import at.ac.uibk.keyless.repositories.SessionRepository;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
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

  /**
   * @param user
   * @return initializes a new session for a user and returns the session-token
   */
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

  /**
   * @param token
   * @return checks if a token is valid and not older than 30 minutes
   */
  public boolean isValidSession(String token) {
    return sessionRepository.findAll().stream()
      .anyMatch(s -> s.getSessionToken().equals(token) &&
        !Minutes.minutesBetween(new DateTime(s.getLastAction()), new DateTime()).isGreaterThan(Minutes.minutes(30)));
  }

  /**
   * Check if a user matches a given session token.
   */
  public boolean userMatchesSession(String session, Long userId) {
    if (session != null && userId != null) {
      return sessionRepository.findByUserId(userId).getSessionToken().equals(session);
    }
    return false;
  }
}
