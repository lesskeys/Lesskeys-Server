package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.KeyRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class KeyService {

  @Autowired
  KeyRepository keyRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;


  public Key getKeyById(Long id) {
    return keyRepository.findByKeyId(id);
  }

  /**
   * Function to edit an existing key.
   * TODO: Extend method for new parameters.
   */
  public void editKey(String username, String keyName, Key newKey) {
    Key toEdit = keyRepository.findKeyForUser(userRepository.findFirstByEmail(username)).stream()
      .filter(k -> k.getKeyName().equals(keyName))
      .findFirst().orElse(null);
    if (toEdit != null) {
      toEdit.setKeyName(newKey.getKeyName());
      toEdit.setHasCustomPermission(newKey.isHasCustomPermission());
    }
    keyRepository.save(toEdit);
  }

  public void registerKey(String aid, String content, String username, String keyName) {
    User owner = userRepository.findFirstByEmail(username);
    Key toSave = new Key();
    toSave.setAid(aid);
    toSave.setContent(content);
    toSave.setOwner(owner);
    toSave.setKeyName(keyName);
    keyRepository.save(toSave);
  }

  public List<Key> getKeysForUser(String username) {
    User operator = userRepository.findFirstByEmail(username);
    if (userService.hasRole(operator, "Admin")) {
      return getAllKeys();
    } else {
      return keyRepository.findKeyForUser(operator);
    }
  }

  public List<Key> getAllKeys() {
    return keyRepository.findAll();
  }
}
