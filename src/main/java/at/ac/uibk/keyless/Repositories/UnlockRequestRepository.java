package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.UnlockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface UnlockRequestRepository extends JpaRepository<UnlockRequest, Long> {

  UnlockRequest findByRequestId(Long requestId);
}
