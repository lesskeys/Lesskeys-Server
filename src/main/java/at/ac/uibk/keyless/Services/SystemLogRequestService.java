package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogRequest;
import at.ac.uibk.keyless.Repositories.SystemLogRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SystemLogRequestService {

  @Autowired
  private SystemLogRequestRepository logRequestRepository;


  public List<SystemLogRequest> getAll() {
    return logRequestRepository.findAll();
  }

  public void createRequest(Lock lock, Date date) {
    SystemLogRequest request = new SystemLogRequest();
    lock.getRelevantUserIds().forEach(request::addUser);
    request.setDay(date);
    logRequestRepository.save(request);
  }
}
