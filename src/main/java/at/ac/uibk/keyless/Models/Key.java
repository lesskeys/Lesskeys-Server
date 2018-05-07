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

  private Byte[] content;

  private int

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

  public Byte[] getContent() {
    return content;
  }

  public void setContent(Byte[] content) {
    this.content = content;
  }
}
