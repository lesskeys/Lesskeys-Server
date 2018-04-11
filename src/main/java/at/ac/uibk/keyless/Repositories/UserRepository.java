package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUserId(Long userId);

  Collection<User> findByFirstName(String firstName);
}
