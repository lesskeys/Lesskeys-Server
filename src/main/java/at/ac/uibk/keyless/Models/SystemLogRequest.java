package at.ac.uibk.keyless.Models;

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

  @Column
  private Map<Long, Boolean> users;

  @Temporal(TemporalType.DATE)
  private Date day;


  public SystemLogRequest() {
    this.users = new HashMap<>();
  }

  public long getRequestId() {
    return requestId;
  }

  public Map<Long, Boolean> getUsers() {
    return users;
  }

  public void setUsers(Map<Long, Boolean> users) {
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
}
