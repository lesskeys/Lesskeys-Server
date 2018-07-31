package at.ac.uibk.keyless.services;

import at.ac.uibk.keyless.models.SystemLogEntry;
import at.ac.uibk.keyless.repositories.SystemLogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SystemLogService {

  @Autowired
  private SystemLogEntryRepository systemLogRepository;

  public void logEvent(String message, String actor, String target) {
    SystemLogEntry toSave = new SystemLogEntry();
    toSave.setLogTime(new Date());
    toSave.setActor(actor);
    toSave.setTarget(target);
    toSave.setEvent(message);
    systemLogRepository.save(toSave);
  }

  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }
}
