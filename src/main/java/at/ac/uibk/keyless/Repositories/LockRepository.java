package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Lukas Dötlinger.
 */
public interface LockRepository extends JpaRepository<Lock, Long> {

  Lock findByLockId(Long lockId);
}
