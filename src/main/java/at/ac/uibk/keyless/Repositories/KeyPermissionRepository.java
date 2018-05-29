package at.ac.uibk.keyless.Repositories;

import at.ac.uibk.keyless.Models.KeyPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Lukas Dötlinger.
 */
@Repository
public interface KeyPermissionRepository extends JpaRepository<KeyPermission, Long> {

  KeyPermission findByPermissionId(Long permissionId);

}
