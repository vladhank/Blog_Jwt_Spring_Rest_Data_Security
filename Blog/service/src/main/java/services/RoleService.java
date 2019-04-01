package services;

import enums.RoleName;
import pojos.Role;

import java.util.Collection;
import java.util.Optional;

public interface RoleService extends IService<Role> {

//    Optional<Role> findByRole(RoleName roleName);

    Role findRoleByName(RoleName roleName);

    Collection<Role> findAll();
}
