package at.ac.uibk.keyless.Models;

import javax.persistence.*;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "lock_keys")
public class Key {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long keyId;

  private byte[] content;

  private byte[] aid;

  @ManyToOne
  private User owner;


  public Long getKeyId() {
    return keyId;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public byte[] getAid() {
    return aid;
  }

  public void setAid(byte[] aid) {
    this.aid = aid;
  }
}
