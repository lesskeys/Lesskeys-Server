package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.LogInEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface LogInEntryRepository extends JpaRepository<LogInEntry, Long> {

  LogInEntry findByLoginId(Long loginId);

  LogInEntry findByUserId(Long userId);

  List<LogInEntry> findByDeviceId(String deviceId);
}
