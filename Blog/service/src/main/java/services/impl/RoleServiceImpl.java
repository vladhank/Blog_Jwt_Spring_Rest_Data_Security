package services.impl;

import enums.RoleName;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Role;
import repositories.RoleRepository;
import services.RoleService;

import java.util.Collection;
import java.util.Optional;

@Service("roleService")
@Transactional
@NoArgsConstructor
public class RoleServiceImpl extends BaseService<Role> implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleByName(RoleName roleName) {
        return roleRepository.findByRole(roleName);
    }

    public Collection<Role> findAll() {
        return roleRepository.findAll();
    }
}
