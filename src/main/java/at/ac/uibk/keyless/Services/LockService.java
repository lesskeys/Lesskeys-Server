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


  public Lock getLockById(long lockId) {
    return lockRepository.findByLockId(lockId);
  }

  public List<Lock> getAllLocks() {
    return lockRepository.findAll();
  }

  public Lock saveLock(Lock lock) {
    return lockRepository.save(lock);
  }

  /**
   * Method to change the IP address of a lock.
   * @param lockId, the id of the lock
   * @param newAddress, the new IP address for the lock
   * @param newName, the new name for the lock
   */
  public void updateAddressAndName(long lockId, String newAddress, String newName) {
    Lock toChange = lockRepository.findByLockId(lockId);
    toChange.setAddress(newAddress);
    toChange.setName(newName);
    lockRepository.save(toChange);
  }

  /**
   * Method to create a new lock.
   * @param creatorId , the userId of the creating user
   * @param code , the code for contacting the lock
   * @return the created lock
   */
  public Lock addLock(long creatorId, String name, String address, String code) {
    Lock newLock = new Lock();
    newLock.setName(name);
    newLock.setAddress(address);
    newLock.setCode(code);
    newLock = saveLock(newLock);
    addUserToLock(newLock.getLockId(), creatorId);
    return newLock;
  }

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
    return lockRepository.findAll().stream()
      .filter(l -> l.getRelevantUserIds().contains(userId))
      .collect(Collectors.toList());
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
