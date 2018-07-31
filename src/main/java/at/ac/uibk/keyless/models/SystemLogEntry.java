package at.ac.uibk.keyless.models;

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
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date logTime;

  @Column(updatable = false)
  private String actor;

  @Column(updatable = false)
  private String target;

  @Column(nullable = false, updatable = false)
  private String event;


  public long getSystemLogId() {
    return systemLogId;
  }

  public Date getLogTime() {
    return logTime;
  }

  public void setLogTime(Date logTime) {
    this.logTime = logTime;
  }

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }
}
