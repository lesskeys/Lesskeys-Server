package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.SystemLogRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface SystemLogRequestRepository extends JpaRepository<SystemLogRequest, Long> {

  SystemLogRequest findByRequestId(long requestId);
}
