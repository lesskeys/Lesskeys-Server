package at.ac.uibk.keyless.Models;

import javax.persistence.*;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "systemLog")
public class SystemLogEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long systemLogId;

  private String event;


  public long getSystemLogId() {
    return systemLogId;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }
}
