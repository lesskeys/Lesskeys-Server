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


  public void saveKey(String aid, String content, String username) {
    User owner = userRepository.findFirstByEmail(username);
    Key toSave = new Key();
    toSave.setAid(aid);
    toSave.setContent(content);
    toSave.setOwner(owner);
    keyRepository.save(toSave);
  }

  public List<Key> getKeysForUser(String username) {
    return keyRepository.findKeyForUser(userRepository.findFirstByEmail(username));
  }

  public List<Key> getAllKeys() {
    return keyRepository.findAll();
  }
}
