package at.ac.uibk.keyless.Models;

/**
 * Created by Lukas Dötlinger.
 */
public enum UserRole {

  ADMIN,
  CUSTODIAN,
  TENANT,
  VISITOR;

  @Override
  public String toString() {
    String role = super.toString();
    return role.charAt(0) + role.substring(1).toLowerCase();
  }
}
