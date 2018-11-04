package at.ac.uibk.keyless.Services;

import at.ac.uibk.keyless.Models.SystemLogEntry;
import at.ac.uibk.keyless.Repositories.SystemLogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Service
public class SystemLogService {

  @Autowired
  private SystemLogEntryRepository systemLogRepository;

  public void logEvent(String message, long ownerId) {
    SystemLogEntry toSave = new SystemLogEntry();
    toSave.setLogTime(new Date());
    toSave.setOwnerId(ownerId);
    toSave.setEvent(message);
    systemLogRepository.save(toSave);
  }

  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }
}
