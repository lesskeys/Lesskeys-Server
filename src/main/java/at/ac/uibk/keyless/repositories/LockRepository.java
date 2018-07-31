package at.ac.uibk.keyless.repositories;

import at.ac.uibk.keyless.models.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface LockRepository extends JpaRepository<Lock, Long> {

  Lock findByLockId(Long lockId);
}
