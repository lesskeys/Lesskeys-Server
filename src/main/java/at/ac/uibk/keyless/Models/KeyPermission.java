package at.ac.uibk.keyless.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Optional;

/**
 * Created by Lukas Dötlinger.
 */
@Entity
public class KeyPermission {

  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long permissionId;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Key key;

  @PostLoad
  public void setupDefaults() {
    mondayFrom    = Optional.ofNullable(mondayFrom   ).orElse("00:00");
    tuesdayFrom   = Optional.ofNullable(tuesdayFrom  ).orElse("00:00");
    wednesdayFrom = Optional.ofNullable(wednesdayFrom).orElse("00:00");
    thursdayFrom  = Optional.ofNullable(thursdayFrom ).orElse("00:00");
    fridayFrom    = Optional.ofNullable(fridayFrom   ).orElse("00:00");
    saturdayFrom  = Optional.ofNullable(saturdayFrom ).orElse("00:00");
    sundayFrom    = Optional.ofNullable(sundayFrom   ).orElse("00:00");

    mondayTo      = Optional.ofNullable(mondayTo     ).orElse("24:00");
    tuesdayTo     = Optional.ofNullable(tuesdayTo    ).orElse("24:00");
    wednesdayTo   = Optional.ofNullable(wednesdayTo  ).orElse("24:00");
    thursdayTo    = Optional.ofNullable(thursdayTo   ).orElse("24:00");
    fridayTo      = Optional.ofNullable(fridayTo     ).orElse("24:00");
    saturdayTo    = Optional.ofNullable(saturdayTo   ).orElse("24:00");
    sundayTo      = Optional.ofNullable(sundayTo     ).orElse("24:00");
  }

  @Column(length = 5)
  private String mondayFrom;
  @Column(length = 5)
  private String tuesdayFrom;
  @Column(length = 5)
  private String wednesdayFrom;
  @Column(length = 5)
  private String thursdayFrom;
  @Column(length = 5)
  private String fridayFrom;
  @Column(length = 5)
  private String saturdayFrom;
  @Column(length = 5)
  private String sundayFrom;

  @Column(length = 5)
  private String mondayTo;
  @Column(length = 5)
  private String tuesdayTo;
  @Column(length = 5)
  private String wednesdayTo;
  @Column(length = 5)
  private String thursdayTo;
  @Column(length = 5)
  private String fridayTo;
  @Column(length = 5)
  private String saturdayTo;
  @Column(length = 5)
  private String sundayTo;


  public KeyPermission() {}

  public KeyPermission(Key key) {
    this.key = key;
  }

  public Long getPermissionId() {
    return permissionId;
  }

  public void setPermissionId(Long permissionId) {
    this.permissionId = permissionId;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getMondayFrom() {
    return mondayFrom;
  }

  public void setMondayFrom(String mondayFrom) {
    this.mondayFrom = mondayFrom;
  }

  public String getTuesdayFrom() {
    return tuesdayFrom;
  }

  public void setTuesdayFrom(String tuesdayFrom) {
    this.tuesdayFrom = tuesdayFrom;
  }

  public String getWednesdayFrom() {
    return wednesdayFrom;
  }

  public void setWednesdayFrom(String wednesdayFrom) {
    this.wednesdayFrom = wednesdayFrom;
  }

  public String getThursdayFrom() {
    return thursdayFrom;
  }

  public void setThursdayFrom(String thursdayFrom) {
    this.thursdayFrom = thursdayFrom;
  }

  public String getFridayFrom() {
    return fridayFrom;
  }

  public void setFridayFrom(String fridayFrom) {
    this.fridayFrom = fridayFrom;
  }

  public String getSaturdayFrom() {
    return saturdayFrom;
  }

  public void setSaturdayFrom(String saturdayFrom) {
    this.saturdayFrom = saturdayFrom;
  }

  public String getSundayFrom() {
    return sundayFrom;
  }

  public void setSundayFrom(String sundayFrom) {
    this.sundayFrom = sundayFrom;
  }

  public String getMondayTo() {
    return mondayTo;
  }

  public void setMondayTo(String mondayTo) {
    this.mondayTo = mondayTo;
  }

  public String getTuesdayTo() {
    return tuesdayTo;
  }

  public void setTuesdayTo(String tuesdayTo) {
    this.tuesdayTo = tuesdayTo;
  }

  public String getWednesdayTo() {
    return wednesdayTo;
  }

  public void setWednesdayTo(String wednesdayTo) {
    this.wednesdayTo = wednesdayTo;
  }

  public String getThursdayTo() {
    return thursdayTo;
  }

  public void setThursdayTo(String thursdayTo) {
    this.thursdayTo = thursdayTo;
  }

  public String getFridayTo() {
    return fridayTo;
  }

  public void setFridayTo(String fridayTo) {
    this.fridayTo = fridayTo;
  }

  public String getSaturdayTo() {
    return saturdayTo;
  }

  public void setSaturdayTo(String saturdayTo) {
    this.saturdayTo = saturdayTo;
  }

  public String getSundayTo() {
    return sundayTo;
  }

  public void setSundayTo(String sundayTo) {
    this.sundayTo = sundayTo;
  }
}
