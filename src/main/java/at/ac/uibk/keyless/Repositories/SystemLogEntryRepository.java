package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.SystemLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface SystemLogEntryRepository extends JpaRepository<SystemLogEntry, Long> {

  SystemLogEntry findBySystemLogId(Long systemLogId);
}
