package MiniSocial.Service;

import MiniSocial.Entity.User;
import MiniSocial.Repository.UserRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class UserService {

    @EJB
    private UserRepository userRepository;

    public User register(User user) {
        // Check if user with email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateProfile(User user) {
        // Verify user exists
        userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userRepository.save(user);
    }

    public boolean authenticate(String email, String password) {
        try {
            User user = findByEmail(email);
            return user != null && user.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }
}