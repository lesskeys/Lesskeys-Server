package at.ac.uibk.keyless.Models;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
@Table(name = "login")
public class LogInEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long loginId;

  @Column(nullable = false)
  private String deviceId;

  @Column(nullable = false, unique = true)
  private long userId;

  @Column(nullable = false)
  private String token;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date date;


  public String getDateAsString() {
    return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
  }

  public long getLoginId() {
    return loginId;
  }

  public void setLoginId(long loginId) {
    this.loginId = loginId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
