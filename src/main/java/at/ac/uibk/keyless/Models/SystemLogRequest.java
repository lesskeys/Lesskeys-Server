package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
public class SystemLogRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long requestId;

  @JsonIgnore
  @ElementCollection
  private Map<Long, Boolean> users;

  @Temporal(TemporalType.DATE)
  private Date day;

  @Column(name = "log_type", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private SystemLogType type;

  private String message;


  public SystemLogRequest() {
    this.users = new HashMap<>();
  }

  public long getRequestId() {
    return requestId;
  }

  public Map<Long, Boolean> getUsers() {
    return users;
  }

  public void setUsers(HashMap<Long, Boolean> users) {
    this.users = users;
  }

  public void addUser(long userId) {
    this.users.put(userId, false);
  }

  public void setUserTrue(long userId) {
    this.users.put(userId, true);
  }

  public Date getDay() {
    return day;
  }

  public void setDay(Date day) {
    this.day = day;
  }

  public SystemLogType getType() {
    return type;
  }

  public void setType(SystemLogType type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
