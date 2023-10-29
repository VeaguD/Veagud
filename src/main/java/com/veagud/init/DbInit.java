package com.veagud.init;

import com.veagud.model.Role;
import com.veagud.model.User;
import com.veagud.service.user.RoleService;
import com.veagud.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;

@Component
public class DbInit {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public DbInit(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    private void postConstruct() {
        String adminRole = "ROLE_ADMIN";
        String userRole = "ROLE_USER";
        Role admin = new Role(adminRole);
        Role user = new Role(userRole);

        roleService.save(admin);
        roleService.save(user);

        Role savedAdmin = roleService.findRoleByName(adminRole);
        Role savedUser = roleService.findRoleByName(userRole);

        User max = new User();

        max.setUsername("admin@gmail.com");
        max.setPassword("admin");
        max.setAge(23);
        max.setFirstName("Максим");
        max.setLastName("Дюгаев");
        max.setRoles(Set.of(savedAdmin, savedUser));
        userService.saveUser(max);

        User kostya = new User();

        kostya.setUsername("admin1@gmail.com");
        kostya.setPassword("admin");
        kostya.setAge(23);
        kostya.setFirstName("Константин");
        kostya.setLastName("Кормашов");
        kostya.setRoles(Set.of(savedAdmin, savedUser));
        userService.saveUser(kostya);

        User second = new User();
        second.setUsername("user@gmail.com");
        second.setPassword("user");
        second.setAge(28);
        second.setFirstName("Андрей");
        second.setLastName("Алексеев");
        second.setRoles(Set.of(savedUser));
        userService.saveUser(second);
    }

    @PreDestroy
    void preDestroy() {
        userService.truncateTable();
        roleService.truncateTable();
    }
}
