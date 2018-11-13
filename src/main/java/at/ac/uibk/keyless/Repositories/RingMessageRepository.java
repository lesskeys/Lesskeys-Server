package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.RingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface RingMessageRepository extends JpaRepository<RingMessage, Long> {

  RingMessage findByMessageId(Long messageId);
}
