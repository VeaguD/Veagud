package com.veagud.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@JsonIgnoreProperties({"authorities", "enabled", "accountNonLocked", "secondName", "firstname", "accountNonExpired", "credentialsNonExpired"})
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true)
    @NotEmpty(message = "Email mustn't be empty")
    @Size(min = 3, max = 100, message = "Email size from 2 to 50 symbols")
    @Email
    private String username;

    @Column
    @NotEmpty(message = "Password must not be empty")
    private String password;

    @Column(name = "first_name")
    @NotEmpty(message = "Firstname shouldn't be empty")
    @Size(min = 2, max = 50, message = "Firstname size from 2 to 50 symbols")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "Surname shouldn't be empty")
    @Size(min = 2, max = 50, message = "Surname size from 2 to 50 symbols")
    private String lastName;

    @Min(value = 8, message = "User must be at least 8 years old.")
    @Max(value = 120, message = "User must be under 120 years.")
    @Column(name = "age")
    private int age;

    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return (roles == null)
                ? new HashSet<>()
                : roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());
    }

    public String rolesToString() {
        StringBuilder rolesList = new StringBuilder();
        this.getRoles()
                .forEach(role -> rolesList.append(role).append(" "));
        return rolesList.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                ", username='" + username + '\'' +
                ", firstname='" + firstName + '\'' +
                ", secondName='" + lastName + '\'' +
                ", age=" + age +
                ", password=" + password +
                ", roles=" + roles +
                '}';
    }
}
