package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.User;
import at.ac.uibk.keyless.Models.UserRole;
import at.ac.uibk.keyless.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  KeyService keyService;


  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserByEmail(String email) {
    return userRepository.findFirstByEmail(email);
  }

  public User getUserById(Long userId) {
    return userRepository.findByUserId(userId);
  }

  public List<User> getActiveSubUsersForUser(User user) {
    return user.getSubUsers().stream()
      .filter(u -> !u.isDisabled())
      .collect(Collectors.toList());
  }

  public User saveUser(User toSave) {
    if (userRepository.findByUserId(toSave.getUserId()) != null) {
      return userRepository.save(toSave);
    } else {
      toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
      return userRepository.save(toSave);
    }
  }

  public boolean hasRole(User user, String role) {
    return user.getRoles().stream().anyMatch(r -> r.toString().equals(role));
  }

  public void disableUser(User user) {
    if (user.getRole().equals("Tenant") || user.getRole().equals("Visitor")) {
      user.setFirstName("-");
      user.setLastName("-");
      user.setDisabledTrue();
      userRepository.save(user);
      // disable all keys
      user.getKeys()
        .forEach(keyService::deactivateKey);
      // repeat the procedure for all sub-users
      user.getSubUsers()
        .forEach(this::disableUser);
    }
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