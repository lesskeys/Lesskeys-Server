package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.Key;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface KeyRepository extends JpaRepository<Key, Long> {

  Key findByKeyId(Long keyId);
}
