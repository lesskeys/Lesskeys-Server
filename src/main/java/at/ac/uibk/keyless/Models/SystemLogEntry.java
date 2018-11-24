package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "system_log")
public class SystemLogEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long systemLogId;

  @Column(name = "log_type", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private SystemLogType type;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @CollectionTable(name = "system_log_owners")
  private List<User> owners;

  @Column(nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date logTime;

  @Column(nullable = false, updatable = false)
  private String event;

  @Column(nullable = false, updatable = false)
  private String actor;


  public SystemLogEntry() {
    this.logTime = new Date();
    this.owners = new ArrayList<>();
  }

  public SystemLogEntry(SystemLogType type) {
    this.logTime = new Date();
    this.owners = new ArrayList<>();
    this.type = type;
  }

  public long getSystemLogId() {
    return systemLogId;
  }

  public SystemLogType getType() {
    return type;
  }

  public List<User> getOwners() {
    return owners;
  }

  public void setOwnerId(List<User> owners) {
    this.owners = owners;
  }

  public void addOwner(User user) {
    this.owners.add(user);
  }

  public boolean isOwner(User user) {
    return this.owners.stream()
      .map(User::getUserId)
      .collect(Collectors.toSet())
      .contains(user.getUserId());
  }

  public Date getLogTime() {
    return logTime;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }
}
