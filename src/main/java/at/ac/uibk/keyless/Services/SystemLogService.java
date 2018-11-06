package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.SystemLogEntryRepository;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SystemLogService {

  @Autowired
  private SystemLogEntryRepository systemLogRepository;

  @Autowired
  private UserService userService;

  public void logEvent(String message, long ownerId) {
    SystemLogEntry toSave = new SystemLogEntry();
    toSave.setLogTime(new Date());
    toSave.setOwnerId(ownerId);
    toSave.setEvent(message);
    systemLogRepository.save(toSave);
  }

  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }

  public Set<SystemLogEntry> getEntriesForUser(long userId) {
    User user = userService.getUserById(userId);

    if (user.getRole().equals("Tenant")) {
      return Sets.union(user.getSubUsers().stream()
        .flatMap(u -> systemLogRepository.findAll().stream()
          .filter(e -> e.getOwnerId() == u.getUserId()))
        .collect(Collectors.toSet()),
        systemLogRepository.findAll().stream()
          .filter(e -> e.getOwnerId() == userId)
          .collect(Collectors.toSet()));
    }
    return systemLogRepository.findAll().stream()
      .filter(e -> e.getOwnerId() == userId ||
        (user.getRole().equals("Admin") && e.getOwnerId() == 0L))
      .collect(Collectors.toSet());
  }
}
