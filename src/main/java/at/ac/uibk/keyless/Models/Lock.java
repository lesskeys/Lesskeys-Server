package at.ac.uibk.keyless.Models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "locks")
public class Lock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long lockId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String address;

  @ElementCollection(targetClass = Long.class, fetch = FetchType.LAZY)
  @CollectionTable(name = "locks_unlockusers")
  private List<Long> relevantUsers;

  @ElementCollection(targetClass = Long.class, fetch = FetchType.LAZY)
  @CollectionTable(name = "locks_unlockkeys")
  private List<Long> relevantKeys;


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

  public List<Long> getRelevantUsers() {
    return relevantUsers;
  }

  public void setRelevantUsers(List<Long> relevantUsers) {
    this.relevantUsers = relevantUsers;
  }

  public List<Long> getRelevantKeys() {
    return relevantKeys;
  }

  public void setRelevantKeys(List<Long> relevantKeys) {
    this.relevantKeys = relevantKeys;
  }
}
