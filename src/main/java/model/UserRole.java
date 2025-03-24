package model;

public class UserRole {
    private Long id;
    private UserRoleEnum userRole;

    public UserRole() {
    }

    public UserRole(Long id, UserRoleEnum userRole) {
        this.id = id;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public UserRole setId(Long id) {
        this.id = id;
        return this;
    }

    public UserRoleEnum getUserRole() {
        return userRole;
    }

    public UserRole setUserRole(UserRoleEnum userRole) {
        this.userRole = userRole;
        return this;
    }
}
