package at.ac.uibk.keyless.Models;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "system_log")
public class SystemLogEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long systemLogId;

  @Column(nullable = false, updatable = false)
  private long ownerId;

  @Column(nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date logTime;

  @Column(nullable = false, updatable = false)
  private String event;


  public long getSystemLogId() {
    return systemLogId;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(long ownerId) {
    this.ownerId = ownerId;
  }

  public Date getLogTime() {
    return logTime;
  }

  public void setLogTime(Date logTime) {
    this.logTime = logTime;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }
}
