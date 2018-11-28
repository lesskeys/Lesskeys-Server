package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

  Session findBySessionId(Long sessionId);

  Session findFirstByUserId(Long userId);
}
