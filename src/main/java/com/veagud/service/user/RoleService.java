package com.veagud.service.user;

import com.veagud.model.Role;
import com.veagud.model.User;

import java.util.Set;

public interface RoleService {
    Set<Role> getRolesSet();

    void save(Role role);

    Role findRoleByName(String name);

    void truncateTable();

    void setUserRoles(User user);


}
