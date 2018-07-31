package at.ac.uibk.keyless.repositories;

import at.ac.uibk.keyless.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUserId(Long userId);

  User findFirstByEmail(String email);

  List<User> findByFirstName(String firstName);
}
