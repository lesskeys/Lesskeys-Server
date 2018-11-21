package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.*;
import at.ac.uibk.keyless.Repositories.SystemLogEntryRepository;
import at.ac.uibk.keyless.Repositories.SystemLogRequestRepository;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SystemLogService {

  @Autowired
  private SystemLogEntryRepository systemLogRepository;

  @Autowired
  private SystemLogRequestService logRequestService;

  @Autowired
  private UserService userService;


  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }

  public Set<SystemLogEntry> getForMainLock() {
    return systemLogRepository.findAll().stream()
      .filter(e -> e.getEvent().startsWith("Lock 1") && e.getType().equals(SystemLogType.UNLOCK))
      .map(e -> {
        SystemLogEntry toReturn = e;
        toReturn.setActor("");
        return toReturn;
      })
      .collect(Collectors.toSet());
  }

  public Set<SystemLogEntry> getRequestedLogs() {
    return logRequestService.getAll().stream()
      .flatMap(request -> getLogsForRequest(request).stream())
      .collect(Collectors.toSet());
  }

  private boolean isSameDay(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);
    return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
      cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
  }

  private Set<SystemLogEntry> getLogsForRequest(SystemLogRequest request) {
    return request.getUsers().keySet().stream()
      .filter(userId -> request.getUsers().get(userId))
      .flatMap(userId -> systemLogRepository.findAll().stream()
        .filter(e -> isSameDay(e.getLogTime(), request.getDay()))
        .filter(e -> e.isOwner(userService.getUserById(userId)) && e.getType().equals(SystemLogType.UNLOCK)))
      .collect(Collectors.toSet());
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

    if (user.getRole().equals("Admin") || user.getRole().equals("Custodian")) {
      Set<SystemLogEntry> requested = getRequestedLogs();
      return Sets.union(systemLogRepository.findAll().stream()
        .filter(e -> e.isOwner(user))
        .collect(Collectors.toSet()),
        Sets.union(requested,
          getForMainLock().stream()
            .filter(e -> requested.stream()
              .map(SystemLogEntry::getSystemLogId)
              .collect(Collectors.toList()).contains(e.getSystemLogId()))
            .collect(Collectors.toSet())));

    } else if (user.getRole().equals("Tenant")) {
      return Sets.union(user.getSubUsers().stream()
        .flatMap(u -> systemLogRepository.findAll().stream()
          .filter(e -> e.isOwner(user)))
        .collect(Collectors.toSet()),
        systemLogRepository.findAll().stream()
          .filter(e -> e.isOwner(user))
          .collect(Collectors.toSet()));

    } else {
      return systemLogRepository.findAll().stream()
        .filter(e -> e.isOwner(user))
        .collect(Collectors.toSet());
    }
  }
}
