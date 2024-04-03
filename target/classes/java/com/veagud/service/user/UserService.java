package com.veagud.service.user;


import com.veagud.model.User;

import java.util.Set;

public interface UserService {
    void saveUser(User user);

    Set<User> getAllUsersSet();

    User findById(long id);

    User findByUsername(String username);

    void updateUser(User user);

    void deleteUser(Long id);

    void truncateTable();

}
