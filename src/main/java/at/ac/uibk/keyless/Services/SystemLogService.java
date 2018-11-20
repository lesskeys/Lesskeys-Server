package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Models.SystemLogType;
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


  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }

  /**
   * Method to create a log of type UNLOCK
   * @param lock, the lock that was unlocked
   * @param actor, either the name of a user or a key that unlocked the lock
   */
  public void logUnlockEvent(Lock lock, String actor) {
    SystemLogEntry toSave = new SystemLogEntry(SystemLogType.UNLOCK);
    lock.getRelevantUsers().stream()
      .filter(user -> !user.getRole().equals("Visitor"))
      .forEach(toSave::addOwner);
    toSave.setEvent("Lock "+lock.getLockId()+" was unlocked");
    toSave.setActor(actor);
    systemLogRepository.save(toSave);
  }

  /**
   * Method to create a log of type LOGIN
   * @param event states whether it was an auto-login or not
   */
  public void logLoginEvent(User user, String event) {
    SystemLogEntry toSave = new SystemLogEntry(SystemLogType.LOGIN);
    toSave.addOwner(user);
    toSave.setEvent(event);
    toSave.setActor("User "+user.getEmail());
    systemLogRepository.save(toSave);
  }

  /**
   * Method to create a log of type SYSTEM
   * @param user, who is responsible for the event
   * @param event states what had happened
   * @param userTarget, a user that has been modified, or null if something else happened
   */
  public void logSystemEvent(User user, String event, User userTarget) {
    SystemLogEntry toSave = new SystemLogEntry(SystemLogType.SYSTEM);
    toSave.addOwner(user);
    if (!(userTarget == null)) { toSave.addOwner(userTarget); }
    toSave.setEvent(event);
    toSave.setActor("User "+user.getEmail());
    systemLogRepository.save(toSave);
  }

  public Set<SystemLogEntry> getEntriesForUser(long userId) {
    User user = userService.getUserById(userId);

    if (user.getRole().equals("Tenant")) {
      return Sets.union(user.getSubUsers().stream()
        .flatMap(u -> systemLogRepository.findAll().stream()
          .filter(e -> e.isOwner(user)))
        .collect(Collectors.toSet()),
        systemLogRepository.findAll().stream()
          .filter(e -> e.isOwner(user))
          .collect(Collectors.toSet()));
    }
    return systemLogRepository.findAll().stream()
      .filter(e -> e.isOwner(user) || user.getRole().equals("Admin"))
      .collect(Collectors.toSet());
  }
}
