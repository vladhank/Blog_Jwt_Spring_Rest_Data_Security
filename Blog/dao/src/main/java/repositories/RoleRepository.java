package repositories;

import enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import pojos.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRole(RoleName roleName);

}
