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
import java.util.stream.Stream;

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

  @Autowired
  private KeyService keyService;


  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }

  public List<SystemLogEntry> getByType(SystemLogType type) {
    return systemLogRepository.findAll().stream()
      .filter(e -> e.getType().toString().equals(type.toString()))
      .collect(Collectors.toList());
  }

  public Set<SystemLogEntry> getForMainLock(User user) {
    return getAll().stream()
      .filter(e -> !e.getActor().startsWith("User "+user.getUserId()))
      .filter(e -> e.getEvent().startsWith("Lock 1") && e.getType().equals(SystemLogType.UNLOCK))
      .map(e -> {
        SystemLogEntry toReturn = new SystemLogEntry(e.getSystemLogId(), e.getType(), e.getLogTime());
        toReturn.setEvent(e.getEvent());
        toReturn.setActor("");
        return toReturn;
      })
      .collect(Collectors.toSet());
  }

  public Set<SystemLogEntry> getRequestedLogs(User user) {
    return logRequestService.getAll().stream()
      .flatMap(request -> getLogsForRequest(request, user).stream())
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

  private Set<SystemLogEntry> getLogsForRequest(SystemLogRequest request, User user) {
    return request.getUsers().keySet().stream()
      // filter only users that have accepted the request and not the requesting user itself
      .filter(userId -> request.getUsers().get(userId) && (userId != user.getUserId()))
      .flatMap(userId -> getByType(request.getType()).stream()
        .filter(e -> isSameDay(e.getLogTime(), request.getDay()))
        .filter(e -> e.isOwner(userService.getUserById(userId))))
      .collect(Collectors.toSet());
  }

  /**
   * Method to create a log of type UNLOCK
   * @param lock, the lock that was unlocked
   * @param actor, either the name of a user or a key that unlocked the lock
   */
  public void logUnlockEvent(Lock lock, String actor, Long userId) {
    SystemLogEntry toSave = new SystemLogEntry(SystemLogType.UNLOCK);
    if (lock.getLockId() != 1L) {
      lock.getRelevantUsers().stream()
        .filter(user -> !user.getRole().equals("Visitor"))
        .forEach(toSave::addOwner);
    } else {
      toSave.addOwner(userService.getUserById(userId));
    }
    toSave.setEvent("Lock "+lock.getLockId()+" was unlocked");
    toSave.setActor(actor);
    systemLogRepository.save(toSave);
  }

  public void logUnlockEventByKey(Lock lock, String actor, Long keyId) {
    SystemLogEntry toSave = new SystemLogEntry(SystemLogType.UNLOCK);
    if (lock.getLockId() != 1L) {
      lock.getRelevantUsers().stream()
        .filter(user -> !user.getRole().equals("Visitor"))
        .forEach(toSave::addOwner);
    } else {
      toSave.addOwner(keyService.getKeyById(keyId).getOwner());
    }
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
    toSave.setActor("User "+user.getUserId());
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
    toSave.setActor("User "+user.getUserId());
    systemLogRepository.save(toSave);
  }

  public Set<SystemLogEntry> getEntriesForUser(long userId) {
    User user = userService.getUserById(userId);

    if (user.getRole().equals("Admin") || user.getRole().equals("Custodian")) {
      Set<SystemLogEntry> requested = getRequestedLogs(user);
      Set<SystemLogEntry> ownedLogs = systemLogRepository.findAll().stream()
        .filter(e -> e.isOwner(user))
        .collect(Collectors.toSet());

      Set<SystemLogEntry> mainLogs = getForMainLock(user).stream()
        .filter(e -> {
          Set<Long> ids = Stream.concat(requested.stream(), ownedLogs.stream())
            .map(SystemLogEntry::getSystemLogId)
            .collect(Collectors.toSet());
          return !ids.contains(e.getSystemLogId());
        })
        .collect(Collectors.toSet());

      return Sets.union(ownedLogs, Sets.union(requested, mainLogs));

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
