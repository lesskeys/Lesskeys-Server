package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

  @Column(name = "keyname",nullable = false)
  private String keyName;

  private String content;

  private String aid;

  @JsonIgnore
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getAid() {
    return aid;
  }

  public void setAid(String aid) {
    this.aid = aid;
  }

  public String getName() {
    return keyName;
  }

  public void setName(String name) {
    this.keyName = name;
  }
}
