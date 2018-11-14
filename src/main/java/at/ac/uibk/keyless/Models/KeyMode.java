package at.ac.uibk.keyless.Models;

/**
 * Created by Lukas Dötlinger.
 */
public enum KeyMode {

  ENABLED,
  DISABLED,
  CUSTOM;

  public String toLowString() {
    String role = super.toString();
    return role.charAt(0) + role.substring(1).toLowerCase();
  }
}
