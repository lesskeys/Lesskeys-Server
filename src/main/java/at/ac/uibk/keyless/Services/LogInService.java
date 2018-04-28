package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.LogInEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.LogInEntryRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class LogInService {

  @Autowired
  private LogInEntryRepository loginRepository;

  @Autowired
  private UserRepository userRepository;

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

  public LogInEntry getForUsername(String username) {
    User user = userRepository.findFirstByEmail(username);
    return loginRepository.findByUserId(user.getUserId());
  }

  /**
   * @param deviceId
   * @return newest entry for deviceId
   */
  public LogInEntry getNewestEntryForDevice(String deviceId) {
    if (loginRepository.findByDeviceId(deviceId).size() > 0) {
      return loginRepository.findByDeviceId(deviceId).stream()
        .sorted(Comparator.comparing(LogInEntry::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
        .collect(Collectors.toList())
        .get(0);
    } else {
      return null;
    }
  }

  public void saveEntry(LogInEntry entry) {
    loginRepository.save(entry);
  }
}
