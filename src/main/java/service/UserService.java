package service;

import model.User;
import repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void createUser(User user) throws SQLException {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty!");
        }else if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("You must specify a password!");
        }else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("You must specify an email!");
        }else if (user.getUserRoles().isEmpty()) {
            throw new IllegalArgumentException("You must specify a user role!");
        }
        userRepository.saveUser(user);
        System.out.println("User created successfully!");
    }
    public void deleteUser(Long id) throws SQLException {
        userRepository.deleteById(id);
        System.out.println("User deleted successfully.");
    }
    public List<User> getAll() throws SQLException {
        return userRepository.findAll();
    }
    public User getUserByUsername(String username) throws SQLException {
        return userRepository.findByUsername(username)
                .orElseThrow(()->new SQLException("User with username "+ username +" not found!"));
    }

    public User getUserById(Long id) throws SQLException {
        return userRepository.findById(id)
                .orElseThrow(()->new SQLException("User with id "+ id +" not found!"));
    }
    public Optional<User> getById(Long id) throws SQLException {
        return userRepository.findById(id);
    }

}
