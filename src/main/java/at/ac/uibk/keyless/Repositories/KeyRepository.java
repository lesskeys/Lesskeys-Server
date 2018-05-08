package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.Key;
import at.ac.uibk.keyless.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface KeyRepository extends JpaRepository<Key, Long> {

  Key findByKeyId(Long keyId);

  @Query("SELECT k FROM Key k WHERE k.owner =:user")
  List<Key> findKeyForUser(@Param("user") User user);
}
