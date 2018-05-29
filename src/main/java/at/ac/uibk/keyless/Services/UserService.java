package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;


  public User getUserByEmail(String email) {
    return userRepository.findFirstByEmail(email);
  }

  public void saveUser(User toSave) {
    if (userRepository.findFirstByEmail(toSave.getEmail()) != null) {

    } else {
      toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
      userRepository.save(toSave);
    }
  }

  public boolean hasRole(User user, String role) {
    return user.getRoles().stream().anyMatch(r -> r.toString().equals(role));
  }

  public void saveEditedUser(User user) {
    userRepository.save(user);
  }
}
