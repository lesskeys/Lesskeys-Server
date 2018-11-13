package at.ac.uibk.keyless.Models;

/**
 * Created by Lukas Dötlinger.
 */
public enum KeyMode {

  ACTIVE,
  BLOCKED,
  CUSTOM;

  @Override
  public String toString() {
    String role = super.toString();
    return role.charAt(0) + role.substring(1).toLowerCase();
  }
}
