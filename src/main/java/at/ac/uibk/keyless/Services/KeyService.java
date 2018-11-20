package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.*;
import at.ac.uibk.keyless.Repositories.KeyRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  @Autowired
  SystemLogService logService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  KeyPermissionService keyPermissionService;


  public Key getKeyById(Long id) {
    return keyRepository.findByKeyId(id);
  }


  public List<Key> getAllKeys() {
    return keyRepository.findAll();
  }

  /**
   * @param id Id of the key to return.
   * @param username Email of the requesting user.
   * @return Return the key if the user is the owner.
   */
  public Key getKeyByIdForUser(Long id, String username) {
    Key k = getKeyById(id);
    if (k.getOwner().getEmail().equals(username)) {
      return k;
    }
    return null;
  }

  /**
   * Method to get a Key by its uid
   * @return a Key or null
   */
  public Key getKeyByUid(String uid) {
    return keyRepository.findAll().stream()
      .filter(k -> k.getUid() != null)
      .filter(k -> k.getUid().equals(uid))
      .findFirst()
      .orElse(null);
  }

  /**
   * Method to set the mode of a Key to BLOCKED
   */
  public void deactivateKey(Key key) {
    key.setMode(KeyMode.DISABLED);
    keyRepository.save(key);
  }

  /**
   * Method to edit an existing key.
   */
  public void editKey(Key key, User user) {
    keyRepository.save(key);
    logService.logSystemEvent(user,"Key "+key.getKeyId()+" was edited", null);
  }

  /**
   * Method to register a new key.
   */
  public void registerKey(String content, String username, Key toSave) {
    User owner = userRepository.findFirstByEmail(username);
    toSave.setContent(passwordEncoder.encode(content));
    toSave.setOwner(owner);
    Key saved = keyRepository.save(toSave);
    keyPermissionService.savePermission(new KeyPermission(saved));
    logService.logEvent("User "+username+" registered key "+saved.getKeyId(),
      userService.getUserByEmail(username).getUserId());
  }

  /**
   * @return all keys where a given user is the owner
   */
  public List<Key> getKeysForUser(String username) {
    User operator = userRepository.findFirstByEmail(username);
    return keyRepository.findKeyForUser(operator);
  }

  /**
   * Method to delete a Key if the operating user is an owner or has the role Admin.
   */
  public void deleteKey(Long keyId, String username) {
    Key toDelete = keyRepository.findByKeyId(keyId);
    if (toDelete.getOwner().getEmail().equals(username) ||
      userService.hasRole(toDelete.getOwner(), "Admin")) {
      keyRepository.delete(toDelete);
      logService.logEvent("User "+username+" deleted key "+toDelete.getKeyName(),
        userService.getUserByEmail(username).getUserId());
    }
  }

  public boolean isValid(Key key) {
    Date current = new Date();
    if (key.getMode().toLowString().equals("Disabled")) {
      return false;
    }
    return (((key.getValidFrom().before(current) && key.getValidTo().after(current))
      && keyPermissionService.isValid(key.getPermission())) || (key.getMode().toLowString().equals("Enabled")));
  }

  public boolean isValidContent(String content, Lock lock) {
    return keyRepository.findAll().stream()
      .filter(this::isValid)
      .filter(k -> k.contentMatches(content))
      .anyMatch(k -> lock.getRelevantKeyIds().contains(k.getKeyId()));
  }
}