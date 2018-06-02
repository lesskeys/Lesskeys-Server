package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.Lock;
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
    return lockRepository.findAll().stream()
      .filter(l -> l.getRelevantUserIds().contains(userId))
      .collect(Collectors.toList());
  }
}
