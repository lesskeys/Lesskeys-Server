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

  @Column(name = "keyname", nullable = false)
  private String keyName;

  private String content;

  private String aid;

  @Column(name = "custom_permission")
  private boolean hasCustomPermission;

  @JsonIgnore
  @ManyToOne
  private User owner;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "key")
  private KeyPermission permission;


  public Long getKeyId() {
    return keyId;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public KeyPermission getPermission() {
    return permission;
  }

  public void setPermission(KeyPermission permission) {
    this.permission = permission;
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

  public String getKeyName() {
    return keyName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  public boolean isHasCustomPermission() {
    return hasCustomPermission;
  }

  public void setHasCustomPermission(boolean hasCustomPermission) {
    this.hasCustomPermission = hasCustomPermission;
  }
}
