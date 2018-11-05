package at.ac.uibk.keyless.Models;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
public class UnlockRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long requestId;

  @Column(nullable = false, updatable = false)
  private Long lockId;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date issued;

  public Long getRequestId() {
    return requestId;
  }

  public Long getLockId() {
    return lockId;
  }

  public void setLockId(Long lockId) {
    this.lockId = lockId;
  }

  public Date getIssued() {
    return issued;
  }

  public void setIssued(Date issued) {
    this.issued = issued;
  }
}
