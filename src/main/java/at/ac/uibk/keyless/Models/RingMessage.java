package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
public class RingMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long messageId;

  @JsonIgnore
  @Column(nullable = false)
  private Long recipientId;

  @Column(updatable = false)
  private String senderName;

  @Column(updatable = false)
  private String message;

  @Column(nullable = false, updatable = false, name = "sent_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;


  public Long getMessageId() {
    return messageId;
  }

  public Long getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(Long recipientId) {
    this.recipientId = recipientId;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
