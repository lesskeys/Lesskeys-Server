package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface LockRepository extends JpaRepository<Lock, Long> {

  Lock findByLockId(Long lockId);

  @Query("SELECT l FROM Lock l WHERE :keyId MEMBER OF l.relevantKeyIds ")
  List<Lock> findByKey(@Param("keyId") Long keyId);
}
