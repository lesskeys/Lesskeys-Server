package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.UnlockRequest;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.UnlockRequestRepository;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class LockRequestService {

  @Autowired
  private LockService lockService;

  @Autowired
  private UnlockRequestRepository unlockRequestRepository;

  @Autowired
  private SystemLogService logService;


  public void sendMessageToLock(long lockId, String message) {
    Lock lock = lockService.getLockById(lockId);
    String[] connectionData = lock.getAddress().split(":");
    try {
      Socket socket = new Socket(connectionData[0], Integer.parseInt(connectionData[1]));
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      output.write(message.getBytes());
      socket.close();
    } catch (Exception e) {
      System.err.println("Failed to request!");
      e.printStackTrace();
    }
  }

  public boolean isToUnlock(long lockId) {
    return unlockRequestRepository.findAll().stream()
      .filter(r -> r.getLockId() == lockId)
      .filter(r -> Minutes.minutesBetween(new DateTime(r.getIssued()), new DateTime()).isLessThan(Minutes.minutes(2)))
      .collect(Collectors.toList()).size() > 0;
  }

  public void logRemoteUnlock(Lock lock, User user) {
    logService.logUnlockEvent(lock, "User " + user.getUserId(), user.getUserId(), true);
  }

  public void addNewUnlockRequest(long lockId) {
    UnlockRequest request = new UnlockRequest();
    request.setLockId(lockId);
    request.setIssued(new Date());
    unlockRequestRepository.save(request);
  }
}
