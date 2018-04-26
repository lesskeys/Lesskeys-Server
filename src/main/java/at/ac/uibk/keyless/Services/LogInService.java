package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.LogInEntry;
import at.ac.uibk.keyless.Repositories.LogInEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class LogInService {

  @Autowired
  private LogInEntryRepository loginRepository;

  public LogInEntry updateLogInEntry(String deviceId, long userId, String token) {
    LogInEntry toSave = loginRepository.findByUserId(userId);
    if (toSave == null) {
      toSave = new LogInEntry();
      toSave.setUserId(userId);
    }
    toSave.setDeviceId(deviceId);
    toSave.setToken(token);
    toSave.setDate(new Date());
    loginRepository.save(toSave);

    return toSave;
  }

  public LogInEntry getFirstLogInEntry(String deviceId) {
    if (loginRepository.findByDeviceId(deviceId).size() > 0) {
      return loginRepository.findByDeviceId(deviceId).get(0);
    } else {
      return null;
    }
  }

}
