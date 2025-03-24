package service;

import model.UserRole;
import model.UserRoleEnum;
import repositories.UserRoleRepository;

import java.sql.SQLException;
import java.util.List;

public class UserRoleService {
    private UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }
    public void createUserRole(UserRole userRole) throws SQLException {
        if(userRole.getUserRole()==null){
            throw new IllegalArgumentException("User role cannot be null!");
        }
        userRoleRepository.saveUserRole(userRole);
        System.out.println("User role created successfully!");
    }
    public void deleteById(Long id) throws SQLException {
        userRoleRepository.deleteById(id);
        System.out.println("Deleted successfully!");
    }
    public UserRole getById(Long id) throws SQLException {
        return userRoleRepository.findById(id)
                .orElseThrow(()->new SQLException("User role with id "+id+" not found!"));

    }
    public UserRole getByUserRole(UserRoleEnum userRoleEnum) throws SQLException {
        return userRoleRepository.findByUserRole(userRoleEnum)
                .orElseThrow(()->new SQLException("User role "+userRoleEnum.name()+" not found!"));

    }
    public List<UserRole> getAll() throws SQLException {
        return userRoleRepository.findAll();
    }

}
