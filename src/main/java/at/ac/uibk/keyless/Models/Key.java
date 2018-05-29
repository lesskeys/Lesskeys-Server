package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

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
  private boolean customPermission;

  @Temporal(TemporalType.DATE)
  private Date validFrom;

  @Temporal(TemporalType.DATE)
  private Date validTo;

  @JsonIgnore
  @ManyToOne
  private User owner;

  /*
  @JsonIgnore
  @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "key")
  private KeyPermission permission;*/

  @PostLoad
  public void setupValidationTimes() {
    Calendar cal = Calendar.getInstance();
    validFrom = Optional.ofNullable(validFrom).orElse(cal.getTime());
    cal.add(Calendar.YEAR, 1);
    validTo   = Optional.ofNullable(validTo  ).orElse(cal.getTime());
  }

  public Long getKeyId() {
    return keyId;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }
  /*
  public KeyPermission getPermission() {
    return permission;
  }

  public void setPermission(KeyPermission permission) {
    this.permission = permission;
  }
  */
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

  public boolean isCustomPermission() {
    return customPermission;
  }

  public void setCustomPermission(boolean customPermission) {
    this.customPermission = customPermission;
  }

  public Date getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(Date validFrom) {
    this.validFrom = validFrom;
  }

  public Date getValidTo() {
    return validTo;
  }

  public void setValidTo(Date validTo) {
    this.validTo = validTo;
  }
}
