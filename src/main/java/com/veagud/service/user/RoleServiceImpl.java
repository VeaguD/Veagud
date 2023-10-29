package com.veagud.service.user;

import com.veagud.model.Role;
import com.veagud.model.User;
import com.veagud.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private RoleRepository rolesRepository;

    @Autowired
    public void setRolesRepository(RoleRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    public Set<Role> getRolesSet() {
        return new HashSet<>(rolesRepository.findAll());
    }

    @Override
    @Transactional
    public void save(Role role) {
        rolesRepository.save(role);
    }

    @Override
    public Role findRoleByName(String name) {
        return rolesRepository.findRoleByName(name);
    }

    @Override
    @Transactional
    public void truncateTable() {
        rolesRepository.deleteAll();
    }

    @Override
    public void setUserRoles(User user) {
        user.setRoles(user.getRoles().stream()
                .map(r -> findRoleByName(r.getName()))
                .collect(Collectors.toSet()));
    }

}
