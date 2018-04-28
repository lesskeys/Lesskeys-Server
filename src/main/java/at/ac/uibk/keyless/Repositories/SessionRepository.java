package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Lukas Dötlinger.
 */
public interface SessionRepository extends JpaRepository<Session, Long> {

  Session findBySessionId(Long sessionId);

  Session findByUserId(Long userId);
}
