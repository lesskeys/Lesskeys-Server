package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogRequest;
import at.ac.uibk.keyless.Models.SystemLogType;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.SystemLogRequestRepository;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SystemLogRequestService {

  @Autowired
  private SystemLogRequestRepository logRequestRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private SessionService sessionService;


  public SystemLogRequest getById(Long requestId) {
    return logRequestRepository.findByRequestId(requestId);
  }

  public void saveRequest(SystemLogRequest request) {
    logRequestRepository.save(request);
    request.getUsers().keySet().stream()
      .map(userId -> userService.getUserById(userId))
      .forEach(u -> notifyUserAboutRequest(u, request));
  }

  public List<SystemLogRequest> getAll() {
    return logRequestRepository.findAll();
  }

  public List<SystemLogRequest> getRequestsForUser(User user) {
    return logRequestRepository.findAll().stream()
      .filter(r -> r.getUsers().keySet().contains(user.getUserId()))
      .filter(r -> !r.getUsers().get(user.getUserId()))
      .collect(Collectors.toList());
  }

  public void createRequest(Lock lock, Date date, String message, boolean notify) {
    SystemLogRequest request = new SystemLogRequest();
    lock.getRelevantUserIds().forEach(request::addUser);
    request.setDay(date);
    request.setMessage(message);
    request.setType(SystemLogType.UNLOCK);
    if (notify) {
      saveRequest(request);
    } else {
      logRequestRepository.save(request);
    }
  }

  public void createRequest(Long userId, Date date, SystemLogType type, String message, boolean notify) {
    SystemLogRequest request = new SystemLogRequest();
    request.addUser(userId);
    request.setDay(date);
    request.setMessage(message);
    request.setType(type);
    if (notify) {
      saveRequest(request);
    } else {
      logRequestRepository.save(request);
    }
  }

  public void acceptRequestForUser(SystemLogRequest request, User user) {
    request.setUserTrue(user.getUserId());
    logRequestRepository.save(request);
  }

  public void notifyUserAboutRequest(User user, SystemLogRequest request) {
    String message = request.getMessage();
    String sender = "System";

    String registrationToken;
    try {
      registrationToken = sessionService.getByUserId(user.getUserId()).getFireBaseToken();
    } catch (NullPointerException e) { return; }
    if (registrationToken == null) {
      return;
    }

    Message toSend = Message.builder()
      .setToken(registrationToken)
      .setAndroidConfig(AndroidConfig.builder()
        .setPriority(AndroidConfig.Priority.HIGH)
        .setNotification(AndroidNotification.builder()
          .setTitle("Anfrage zur Log-Einsicht")
          .setBody(message)
          .setIcon("stock_ticker_update")
          .setClickAction("at.ac.uibk.keylessapp_TARGET_SYSTEMLOG_NOTIFICATION")
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
