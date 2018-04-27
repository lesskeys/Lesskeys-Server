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

  public void logEvent(String message) {
    String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    SystemLogEntry toSave = new SystemLogEntry();
    toSave.setEvent(date+" "+message);
    systemLogRepository.save(toSave);
  }

  public List<SystemLogEntry> getAll() {
    return systemLogRepository.findAll();
  }
}
