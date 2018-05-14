package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  public void saveUser(User toSave) {
    if (userRepository.findFirstByEmail(toSave.getEmail()) != null) {

    } else {
      toSave.setPassword(new BCryptPasswordEncoder().encode(toSave.getPassword()));
      userRepository.save(toSave);
    }
  }
}
