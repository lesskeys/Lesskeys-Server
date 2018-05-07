package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.KeyRepository;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class KeyService {

  @Autowired
  KeyRepository keyRepository;

  @Autowired
  UserRepository userRepository;


  public void saveKey(byte[] aid, byte[] content, String username) {
    User owner = userRepository.findFirstByEmail(username);
    Key toSave = new Key();
    toSave.setAid(aid);
    toSave.setContent(content);
    toSave.setOwner(owner);
    keyRepository.save(toSave);
  }
}
