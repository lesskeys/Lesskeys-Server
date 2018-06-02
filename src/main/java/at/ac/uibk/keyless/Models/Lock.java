package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "locks")
public class Lock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long lockId;

  @Column(name = "lock_name", nullable = false)
  private String name;

  @Column(nullable = false)
  private String address;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @CollectionTable(name = "locks_unlockusers")
  private List<User> relevantUsers;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @CollectionTable(name = "locks_unlockkeys")
  private List<Key> relevantKeys;


  public Long getLockId() {
    return lockId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public List<User> getRelevantUsers() {
    return relevantUsers;
  }

  public List<Long> getRelevantUserIds() {
    return relevantUsers.stream()
      .map(u -> u.getUserId())
      .collect(Collectors.toList());
  }

  public void setRelevantUsers(List<User> relevantUsers) {
    this.relevantUsers = relevantUsers;
  }

  public List<Key> getRelevantKeys() {
    return relevantKeys;
  }

  public List<Long> getRelevantKeyIds() {
    return relevantKeys.stream()
      .map(k -> k.getKeyId())
      .collect(Collectors.toList());
  }

  public void setRelevantKeys(List<Key> relevantKeys) {
    this.relevantKeys = relevantKeys;
  }
}
