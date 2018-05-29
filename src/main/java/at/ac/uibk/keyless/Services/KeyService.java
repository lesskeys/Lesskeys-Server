package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.KeyRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
  public void editKey(Long keyId, Key newKey) {
    Key toEdit = keyRepository.findByKeyId(keyId);
    if (toEdit != null) {
      toEdit.setKeyName(newKey.getKeyName());
      toEdit.setCustomPermission(newKey.isCustomPermission());
      toEdit.setValidFrom(newKey.getValidFrom());
      toEdit.setValidTo(newKey.getValidTo());
      keyRepository.save(toEdit);
    }
  }

  public void registerKey(String aid, String content, String username, String keyName) {
    User owner = userRepository.findFirstByEmail(username);
    Key toSave = new Key();
    toSave.setAid(aid);
    toSave.setContent(content);
    toSave.setOwner(owner);
    toSave.setKeyName(keyName);
    toSave.setCustomPermission(false);
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

  /**
   * Method to delete a Key if the operating user is an owner or has the role Admin.
   */
  public void deleteKey(Long keyId, String username) {
    Key toDelete = keyRepository.findByKeyId(keyId);
    if (toDelete.getOwner().getEmail().equals(username) ||
      userService.hasRole(toDelete.getOwner(), "Admin")) {
      keyRepository.delete(toDelete);
    }
  }

  public boolean isValid(Key key) {
    Date current = new Date();
    if (!key.isCustomPermission() || (key.getValidFrom().before(current) && key.getValidTo().after(current))) {
      return true;
    } else {
      return false;
    }
  }
}
