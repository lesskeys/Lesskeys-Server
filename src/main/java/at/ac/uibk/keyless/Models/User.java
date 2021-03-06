package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Lukas Dötlinger.
 */

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long userId;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  @JsonIgnore
  private String password;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Date createdAt;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @JsonIgnore
  @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Key> keys;

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "creator_id")
  private User creator;

  @JsonIgnore
  @OneToMany(mappedBy = "creator", orphanRemoval = true)
  @CollectionTable(name = "users_subusers")
  private List<User> subUsers;

  @JsonIgnore
  @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles")
  @Enumerated(EnumType.STRING)
  private Set<UserRole> roles;

  @JsonIgnore
  @Column(nullable = false)
  private String disabled;


  public User() {
    this.disabled = "ENABLED";
  }

  public String getRole() {
    Set<UserRole> r = getRoles();
    if (r.contains(UserRole.ADMIN)) {
      return UserRole.ADMIN.toString();
    } else if (r.contains(UserRole.CUSTODIAN)) {
      return UserRole.CUSTODIAN.toString();
    } else if (r.contains(UserRole.TENANT)) {
      return UserRole.TENANT.toString();
    } else {
      return UserRole.VISITOR.toString();
    }
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public List<Key> getKeys() {
    return keys;
  }

  public void setKeys(List<Key> keys) {
    this.keys = keys;
  }

  public List<User> getSubUsers() {
    return subUsers;
  }

  public void setSubUsers(List<User> subUsers) {
    this.subUsers = subUsers;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Set<UserRole> getRoles() {
    return roles;
  }

  public void setRoles(Set<UserRole> roles) {
    this.roles = roles;
  }

  public boolean isDisabled() {
    return disabled.equals("DISABLED");
  }

  public void setDisabledTrue() {
    this.disabled = "DISABLED";
  }
}
