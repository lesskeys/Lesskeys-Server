package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogRequest;
import at.ac.uibk.keyless.Models.SystemLogType;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.SystemLogRequestRepository;
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


  public SystemLogRequest getById(Long requestId) {
    return logRequestRepository.findByRequestId(requestId);
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

  public void createRequest(Lock lock, Date date, SystemLogType type) {
    SystemLogRequest request = new SystemLogRequest();
    lock.getRelevantUserIds().forEach(request::addUser);
    request.setDay(date);
    request.setType(type);
    logRequestRepository.save(request);
  }

  public void createRequest(Long userId, Date date, SystemLogType type) {
    SystemLogRequest request = new SystemLogRequest();
    request.addUser(userId);
    request.setDay(date);
    request.setType(type);
    logRequestRepository.save(request);
  }

  public void acceptRequestForUser(SystemLogRequest request, User user) {
    request.setUserTrue(user.getUserId());
    logRequestRepository.save(request);
  }
}
