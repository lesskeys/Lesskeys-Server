package at.ac.uibk.keyless.Models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sessionId;

  @Column(nullable = false, unique = true)
  private Long userId;

  @Column(nullable = false)
  private String sessionToken;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastAction;


  public Long getSessionId() {
    return sessionId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }

  public Date getLastAction() {
    return lastAction;
  }

  public void setLastAction(Date lastAction) {
    this.lastAction = lastAction;
  }
}
