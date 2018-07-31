package at.ac.uibk.keyless.repositories;

import at.ac.uibk.keyless.models.KeyPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface KeyPermissionRepository extends JpaRepository<KeyPermission, Long> {

  KeyPermission findByPermissionId(Long permissionId);

}
