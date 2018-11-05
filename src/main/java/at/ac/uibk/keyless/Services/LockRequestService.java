package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class LockRequestService {

  @Autowired
  private LockService lockService;


  public void sendMessageToLock(long lockId, String message) {
    Lock lock = lockService.getLockById(lockId);
    String[] connectionData = lock.getAddress().split(":");
    try {
      Socket socket = new Socket(connectionData[0], Integer.parseInt(connectionData[1]));
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      output.writeUTF(message);
      socket.close();
    } catch (Exception e) {
      System.err.println("Failed to request!");
      e.printStackTrace();
    }
  }
}
