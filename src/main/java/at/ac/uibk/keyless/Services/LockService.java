package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.Lock;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.KeyRepository;
import at.ac.uibk.keyless.Repositories.LockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class LockService {

  @Autowired
  LockRepository lockRepository;

  @Autowired
  KeyRepository keyRepository;

  @Autowired
  UserService userService;


  /**
   * @return locks for which a given keyId is valid.
   */
  public List<Lock> getLocksForKey(Long keyId) {
    return lockRepository.findAll().stream()
      .filter(l -> l.getRelevantKeyIds().contains(keyId))
      .collect(Collectors.toList());
  }

  /**
   * @return locks for which a given userId is valid.
   */
  public List<Lock> getLocksForUser(Long userId) {
    User operator = userService.getUserById(userId);
    if (userService.hasRole(operator, "Admin")) {
      return lockRepository.findAll();
    } else {
      return lockRepository.findAll().stream()
        .filter(l -> l.getRelevantUserIds().contains(userId))
        .collect(Collectors.toList());
    }
  }

  public Lock getLockForIdAndCode(Long lockId, String code) {
    Lock lock = lockRepository.findByLockId(lockId);
    return lock.getCode().equals(code) ? lock : null;
  }

  public void addUserToLock(Long lockId, Long userId) {
    Lock lock = lockRepository.findByLockId(lockId);
    lock.addRelevantUser(userService.getUserById(userId));
    lockRepository.save(lock);
  }

  public void addUserToLocks(List<Object> lockIds, Long userId) {
    if (lockIds != null) {
      for (Object o : lockIds) {
        addUserToLock(Long.parseLong(o.toString()), userId);
      }
    }
  }

  public void addKeyToLock(Long lockId, Long keyId) {
    Lock lock = lockRepository.findByLockId(lockId);
    lock.addRelevantKey(keyRepository.findByKeyId(keyId));
    lockRepository.save(lock);
  }

  public void addKeysToLocks(List<Object> lockIds, Long keyId) {
    if (lockIds != null) {
      for (Object o : lockIds) {
        addKeyToLock(Long.parseLong(o.toString()), keyId);
      }
    }
  }

  public void removeUserFromLocks(Long userId) {
    lockRepository.findAll()
      .forEach(l -> {
        l.removeRelevantUser(userService.getUserById(userId));
        lockRepository.save(l);
      });
  }

  public void removeKeyFromLocks(Long keyId) {
    lockRepository.findAll()
      .forEach(l -> {
        l.removeRelevantKey(keyRepository.findByKeyId(keyId));
        lockRepository.save(l);
      });
  }
}
