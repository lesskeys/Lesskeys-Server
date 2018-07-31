package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.User;
import at.ac.uibk.keyless.models.UserRole;
import at.ac.uibk.keyless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

  public User getUserById(Long userId) {
    return userRepository.findByUserId(userId);
  }

  public void saveUser(User toSave) {
    if (userRepository.findByUserId(toSave.getUserId()) != null) {
      userRepository.save(toSave);
    } else {
      toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
      userRepository.save(toSave);
    }
  }

  public boolean hasRole(User user, String role) {
    return user.getRoles().stream().anyMatch(r -> r.toString().equals(role));
  }

  public String editUsersPassword(User user, String oldPw, String newPw1, String newPw2) {
    if (passwordEncoder.matches(oldPw, user.getPassword()) && newPw1.equals(newPw2)) {
      user.setPassword(passwordEncoder.encode(newPw1));
      userRepository.save(user);
      return "Success";
    } else {
      return "Failure";
    }
  }

  public UserRole getRoleForString(String role) {
    switch (role) {
      case "Admin": return UserRole.ADMIN;
      case "Custodian": return UserRole.CUSTODIAN;
      case "Tenant": return UserRole.TENANT;
      case "Visitor": return UserRole.VISITOR;
      default: return UserRole.VISITOR;
    }
  }
}